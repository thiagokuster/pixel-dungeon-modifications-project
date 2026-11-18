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
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShamanMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;

public class WeaknessTotem extends Mob {

	public int left = 6;
	public int radius = 2;
	public boolean roots = false;
	public int shield = 0;

	{
		spriteClass = StatueSprite.class;
		HP = HT = 20;
		defenseSkill = 0;
		EXP = 0;
		maxLvl = -5;
		alignment = Alignment.ALLY;
		state = PASSIVE;
		properties.add(Property.IMMOVABLE);
		properties.add(Property.INORGANIC);
	}

	@Override
	protected boolean act() {
		if (left-- <= 0) {
			die(this);
			return true;
		}
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (mob != this && mob.alignment == Alignment.ENEMY && mob.isAlive()
					&& Dungeon.level.distance(pos, mob.pos) <= radius) {
				Buff.prolong(mob, Weakness.class, 4f);
				Buff.prolong(mob, ShamanMark.class, 4f);
				if (roots) Buff.prolong(mob, Roots.class, 2f);
			}
		}
		Hero hero = Dungeon.hero;
		if (hero != null && hero.isAlive() && shield > 0 && Dungeon.level.distance(pos, hero.pos) <= radius + 1) {
			Buff.affect(hero, Barrier.class).incShield(shield);
			if (hero.subClass == HeroSubClass.SPIRIT_SHAMAN || hero.pointsInTalent(Talent.SPIRIT_MEND) > 0) {
				int heal = 1 + hero.pointsInTalent(Talent.GENTLE_CURRENT);
				hero.HP = Math.min(hero.HT, hero.HP + heal);
			}
		}
		spend(TICK);
		return true;
	}

	@Override
	public boolean reset() {
		return true;
	}
}
