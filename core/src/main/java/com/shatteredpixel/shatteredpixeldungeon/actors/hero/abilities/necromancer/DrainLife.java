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
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.necromancer;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class DrainLife extends ArmorAbility {

	{
		baseChargeUse = 35f;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		if (target == null) return;
		int range = 5 + hero.pointsInTalent(Talent.DRAIN_REACH);
		if (Dungeon.level.distance(hero.pos, target) > range) {
			GLog.w(Messages.get(this, "too_far"));
			return;
		}
		Char enemy = Actor.findChar(new Ballistica(hero.pos, target, Ballistica.MAGIC_BOLT).collisionPos);
		if (enemy == null || !enemy.isAlive() || enemy.alignment != Char.Alignment.ENEMY) {
			GLog.w(Messages.get(this, "no_target"));
			return;
		}
		int damage = 8 + hero.lvl / 2 + 3 * hero.pointsInTalent(Talent.DRAIN_BURST);
		if (hero.subClass == HeroSubClass.VAMPIRE) damage += 3;
		enemy.damage(damage, hero);
		float part = hero.subClass == HeroSubClass.VAMPIRE ? 0.55f : 0.35f;
		part += 0.08f * hero.pointsInTalent(Talent.DEEP_DRINK);
		part += 0.06f * hero.pointsInTalent(Talent.DRAIN_LEECH);
		part += 0.04f * hero.pointsInTalent(Talent.BLOOD_PACT);
		int heal = Math.max(1, Math.round(damage * part));
		hero.HP = Math.min(hero.HT, hero.HP + heal);
		hero.sprite.showStatus(CharSprite.POSITIVE, "+" + heal);
		Sample.INSTANCE.play(Assets.Sounds.CURSED);
		hero.sprite.zap(enemy.pos);
		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		Invisibility.dispel();
		hero.spendAndNext(1f);
	}

	@Override
	public int icon() {
		return HeroIcon.DEATH_MARK;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.DRAIN_REACH, Talent.DRAIN_BURST, Talent.DRAIN_LEECH, Talent.HEROIC_ENERGY};
	}
}
