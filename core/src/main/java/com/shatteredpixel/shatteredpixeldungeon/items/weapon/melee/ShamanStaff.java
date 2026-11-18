/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Pixel Dungeon Alterado
 * Copyright (C) 2026 thiagokuster
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShamanMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ShamanStaff extends MeleeWeapon {

	public static final String AC_CAST = "CAST";

	public int charges = 3;
	public int maxCharges = 3;
	public float partialCharge = 0;

	{
		image = ItemSpriteSheet.MAGES_STAFF;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.1f;
		tier = 1;
		unique = true;
		bones = false;
	}

	@Override
	public int max(int lvl) {
		return 4 * (tier + 1) + lvl * (tier + 1);
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);
		Buff.affect(ch, Charger.class);
	}

	private static final String CHARGES = "shaman_charges";
	private static final String PARTIAL = "shaman_partial";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGES, charges);
		bundle.put(PARTIAL, partialCharge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charges = bundle.getInt(CHARGES);
		partialCharge = bundle.getFloat(PARTIAL);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_CAST);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals(AC_CAST)) {
			if (charges <= 0) {
				GLog.w(Messages.get(this, "no_charge"));
				return;
			}
			curUser = hero;
			curItem = this;
			GameScene.selectCell(caster);
			return;
		}
		super.execute(hero, action);
	}

	@Override
	public String status() {
		return charges + "/" + maxCharges;
	}

	@Override
	public String info() {
		return super.info() + "\n\n" + Messages.get(this, "charge_desc", charges, maxCharges);
	}

	public static int markDuration(Hero hero) {
		int duration = 6;
		if (hero.subClass == HeroSubClass.SPIRIT_SHAMAN) duration += 3;
		duration += 2 * hero.pointsInTalent(Talent.LONG_MARK);
		duration += hero.pointsInTalent(Talent.MARKED_PREY);
		duration += hero.pointsInTalent(Talent.SPIRIT_BOND);
		return duration;
	}

	public float turnsToCharge(Hero hero) {
		float speed = 1f + 0.2f * hero.pointsInTalent(Talent.QUICK_STAFF);
		speed += 0.15f * hero.pointsInTalent(Talent.STAFF_RHYTHM);
		return 8f / speed;
	}

	public int castRange(Hero hero) {
		return 4 + hero.pointsInTalent(Talent.STAFF_REACH);
	}

	private void zap(Hero hero, int cell) {
		if (charges <= 0) return;
		if (Dungeon.level.distance(hero.pos, cell) > castRange(hero)) {
			GLog.w(Messages.get(MeleeWeapon.class, "ability_target_range"));
			return;
		}
		Ballistica shot = new Ballistica(hero.pos, cell, Ballistica.MAGIC_BOLT);
		Char target = Actor.findChar(shot.collisionPos);
		charges--;
		updateQuickslot();
		hero.sprite.zap(shot.collisionPos);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
		MagicMissile.boltFromChar(hero.sprite.parent, MagicMissile.BEACON, hero.sprite, shot.collisionPos, new Callback() {
			@Override
			public void call() {
				if (target != null && target.alignment == Char.Alignment.ENEMY && target.isAlive()) {
					int duration = markDuration(hero);
					Buff.prolong(target, ShamanMark.class, duration);
					Buff.prolong(target, Weakness.class, duration);
					if (hero.pointsInTalent(Talent.NATURES_GRIP) > 0) {
						Buff.prolong(target, Slow.class, 2f * hero.pointsInTalent(Talent.NATURES_GRIP));
					}
				}
				int shield = 2 * hero.pointsInTalent(Talent.WARDING_WOOD);
				if (hero.subClass == HeroSubClass.SPIRIT_SHAMAN) shield += 2;
				shield += hero.pointsInTalent(Talent.SPIRIT_VEIL);
				if (shield > 0) {
					Buff.affect(hero, Barrier.class).incShield(shield);
				}
				if (Random.Float() < 0.2f * hero.pointsInTalent(Talent.STAFF_SURGE) && charges < maxCharges) {
					charges++;
					updateQuickslot();
				}
				Invisibility.dispel(hero);
				hero.spendAndNext(1f);
			}
		});
	}

	private final CellSelector.Listener caster = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (cell != null && curUser instanceof Hero) {
				zap((Hero) curUser, cell);
			}
		}

		@Override
		public String prompt() {
			return Messages.get(ShamanStaff.class, "prompt");
		}
	};

	public static class Charger extends Buff {
		@Override
		public boolean act() {
			if (target instanceof Hero && target.buff(MagicImmune.class) == null) {
				ShamanStaff staff = ((Hero) target).belongings.getItem(ShamanStaff.class);
				if (staff != null && staff.charges < staff.maxCharges) {
					staff.partialCharge += 1f / staff.turnsToCharge((Hero) target);
					while (staff.partialCharge >= 1f && staff.charges < staff.maxCharges) {
						staff.partialCharge--;
						staff.charges++;
						staff.updateQuickslot();
					}
				}
			}
			spend(TICK);
			return true;
		}
	}
}
