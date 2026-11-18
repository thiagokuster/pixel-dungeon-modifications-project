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

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.watabou.utils.Random;

public class RaisedSkeleton extends Mob {

	{
		spriteClass = SkeletonSprite.class;
		HP = HT = 8;
		defenseSkill = 4;
		EXP = 0;
		maxLvl = -5;
		alignment = Alignment.ALLY;
		state = HUNTING;
		intelligentAlly = true;
		properties.add(Property.UNDEAD);
		properties.add(Property.INORGANIC);
	}

	public void setPower(int hp, int turns) {
		HT = HP = Math.max(1, hp);
		Buff.affect(this, Fades.class).left = Math.max(1, turns);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(1, 4);
	}

	@Override
	public int attackSkill(com.shatteredpixel.shatteredpixeldungeon.actors.Char target) {
		return 8;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}

	public static class Fades extends Buff {
		public int left = 15;

		@Override
		public boolean act() {
			left--;
			if (left <= 0 && target.isAlive()) {
				target.die(this);
			}
			spend(TICK);
			return true;
		}
	}
}
