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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RaisedSkeleton;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class NecroStaff extends MeleeWeapon {

	public static final String AC_SUMMON = "SUMMON";
	public static final String AC_DRAIN = "DRAIN";

	public int charges = 2;
	public int maxCharges = 2;
	public float partialCharge = 0;

	{
		image = ItemSpriteSheet.MAGES_STAFF;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 0.9f;
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

	private static final String CHARGES = "necro_charges";
	private static final String PARTIAL = "necro_partial";

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
		actions.add(AC_SUMMON);
		actions.add(AC_DRAIN);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals(AC_SUMMON)) {
			if (!spendCharge(hero)) return;
			if (!summon(hero, false)) {
				charges++;
				updateQuickslot();
				GLog.w(Messages.get(this, "no_space"));
				return;
			}
			if (Random.Float() < 0.25f * hero.pointsInTalent(Talent.SECOND_RISING)
					+ 0.2f * hero.pointsInTalent(Talent.PACK_OF_BONES)) {
				summon(hero, true);
			}
			Invisibility.dispel(hero);
			hero.sprite.operate(hero.pos);
			hero.spendAndNext(1f);
			return;
		}
		if (action.equals(AC_DRAIN)) {
			if (charges <= 0) {
				GLog.w(Messages.get(this, "no_charge"));
				return;
			}
			curUser = hero;
			curItem = this;
			GameScene.selectCell(drainer);
			return;
		}
		super.execute(hero, action);
	}

	private boolean spendCharge(Hero hero) {
		if (charges <= 0) {
			GLog.w(Messages.get(this, "no_charge"));
			return false;
		}
		charges--;
		updateQuickslot();
		return true;
	}

	public static int skeletonHp(Hero hero) {
		int hp = 8;
		hp += 4 * hero.pointsInTalent(Talent.THICK_BONE);
		hp += 3 * hero.pointsInTalent(Talent.STRONGER_DEAD);
		if (hero.subClass == HeroSubClass.DEATH_LORD) hp += 8;
		hp += 2 * hero.pointsInTalent(Talent.LORD_BONE);
		hp += 3 * hero.pointsInTalent(Talent.SUMMON_MIGHT);
		return hp;
	}

	public static int skeletonTurns(Hero hero) {
		int turns = 16;
		turns += 4 * hero.pointsInTalent(Talent.LONGER_DEAD);
		turns += 4 * hero.pointsInTalent(Talent.ENDURING_DEAD);
		if (hero.subClass == HeroSubClass.DEATH_LORD) turns += 8;
		return turns;
	}

	public static boolean summon(Hero hero, boolean weaker) {
		ArrayList<Integer> spots = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = hero.pos + offset;
			if (Actor.findChar(cell) == null && Dungeon.level.passable[cell]) {
				spots.add(cell);
			}
		}
		if (spots.isEmpty()) return false;
		RaisedSkeleton skeleton = new RaisedSkeleton();
		int hp = skeletonHp(hero);
		if (weaker) hp = Math.max(4, hp / 2);
		skeleton.setPower(hp, skeletonTurns(hero));
		int guard = 3 * hero.pointsInTalent(Talent.BONE_ARMOR) + 2 * hero.pointsInTalent(Talent.SUMMON_GUARD);
		if (guard > 0) {
			Buff.affect(skeleton, Barrier.class).incShield(guard);
		}
		skeleton.pos = Random.element(spots);
		GameScene.add(skeleton);
		Dungeon.level.occupyCell(skeleton);
		Sample.INSTANCE.play(Assets.Sounds.CURSED);
		return true;
	}

	private void drain(Hero hero, int cell) {
		int range = 4 + hero.pointsInTalent(Talent.DRAIN_REACH);
		if (Dungeon.level.distance(hero.pos, cell) > range) {
			GLog.w(Messages.get(MeleeWeapon.class, "ability_target_range"));
			return;
		}
		Ballistica shot = new Ballistica(hero.pos, cell, Ballistica.MAGIC_BOLT);
		Char target = Actor.findChar(shot.collisionPos);
		if (target == null || target.alignment != Char.Alignment.ENEMY || !target.isAlive()) {
			GLog.w(Messages.get(this, "no_target"));
			return;
		}
		charges--;
		updateQuickslot();
		int damage = 4 + 2 * buffedLvl() + 2 * hero.pointsInTalent(Talent.DRAIN_BURST);
		if (hero.subClass == HeroSubClass.VAMPIRE) damage += 2;
		target.damage(damage, this);
		float healPart = 0.40f;
		if (hero.subClass == HeroSubClass.VAMPIRE) healPart = 0.60f;
		healPart += 0.08f * hero.pointsInTalent(Talent.DEEP_DRINK);
		healPart += 0.06f * hero.pointsInTalent(Talent.EFFICIENT_DRINK);
		healPart += 0.05f * hero.pointsInTalent(Talent.RICHER_DRINK);
		healPart += 0.04f * hero.pointsInTalent(Talent.BLOOD_PACT);
		int heal = Math.max(1, Math.round(damage * healPart));
		hero.HP = Math.min(hero.HT, hero.HP + heal);
		hero.sprite.showStatus(CharSprite.POSITIVE, "+" + heal);
		if (hero.pointsInTalent(Talent.BLOOD_BARRIER) > 0) {
			Buff.affect(hero, Barrier.class).incShield(2 * hero.pointsInTalent(Talent.BLOOD_BARRIER));
		}
		if (hero.pointsInTalent(Talent.LAST_RITE) > 0) {
			Buff.prolong(target, Vulnerable.class, 3f * hero.pointsInTalent(Talent.LAST_RITE));
		}
		Invisibility.dispel(hero);
		hero.sprite.zap(target.pos);
		Sample.INSTANCE.play(Assets.Sounds.HIT);
		hero.spendAndNext(1f);
	}

	@Override
	public String status() {
		return charges + "/" + maxCharges;
	}

	@Override
	public String info() {
		return super.info() + "\n\n" + Messages.get(this, "charge_desc", charges, maxCharges);
	}

	private final CellSelector.Listener drainer = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (cell != null && curUser instanceof Hero) {
				drain((Hero) curUser, cell);
			}
		}

		@Override
		public String prompt() {
			return Messages.get(NecroStaff.class, "prompt");
		}
	};

	public static class Charger extends Buff {
		@Override
		public boolean act() {
			if (target instanceof Hero && target.buff(MagicImmune.class) == null) {
				NecroStaff staff = ((Hero) target).belongings.getItem(NecroStaff.class);
				if (staff != null && staff.charges < staff.maxCharges) {
					float speed = 1f + 0.15f * ((Hero) target).pointsInTalent(Talent.GRAVE_PACT);
					staff.partialCharge += speed / 12f;
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
