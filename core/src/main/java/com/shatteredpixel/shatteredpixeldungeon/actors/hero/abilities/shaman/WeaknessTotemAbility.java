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
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.shaman;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.WeaknessTotem;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WeaknessTotemAbility extends ArmorAbility {

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
		int cell = target;
		if (Actor.findChar(cell) != null || !Dungeon.level.passable[cell]) {
			ArrayList<Integer> spots = new ArrayList<>();
			for (int offset : PathFinder.NEIGHBOURS8) {
				int neighbor = cell + offset;
				if (Actor.findChar(neighbor) == null && Dungeon.level.passable[neighbor]) spots.add(neighbor);
			}
			if (spots.isEmpty()) {
				GLog.w(Messages.get(MeleeWeapon.class, "ability_target_range"));
				return;
			}
			cell = Random.element(spots);
		}
		WeaknessTotem totem = new WeaknessTotem();
		totem.left = 6 + hero.pointsInTalent(Talent.TOTEM_ECHO) + hero.pointsInTalent(Talent.TOTEM_HEART) + hero.pointsInTalent(Talent.SPIRIT_BOND);
		if (hero.subClass == HeroSubClass.SPIRIT_SHAMAN) totem.left += 3;
		totem.radius = 2 + hero.pointsInTalent(Talent.TOTEM_REACH) + hero.pointsInTalent(Talent.WIDE_TOTEM);
		totem.roots = hero.pointsInTalent(Talent.TOTEM_ROOT) > 0;
		totem.shield = hero.pointsInTalent(Talent.GENTLE_CURRENT);
		if (hero.subClass == HeroSubClass.SPIRIT_SHAMAN) totem.shield += 1;
		totem.pos = cell;
		GameScene.add(totem);
		Dungeon.level.occupyCell(totem);
		Sample.INSTANCE.play(Assets.Sounds.PLANT);
		hero.sprite.operate(hero.pos);
		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		Invisibility.dispel();
		hero.spendAndNext(1f);
	}

	@Override
	public int icon() {
		return HeroIcon.NATURES_POWER;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.WIDE_TOTEM, Talent.TOTEM_ROOT, Talent.TOTEM_ECHO, Talent.HEROIC_ENERGY};
	}
}
