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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RaisedSkeleton;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.NecroStaff;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;

public class GuardianSpirit extends ArmorAbility {

	{
		baseChargeUse = 50f;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		int shield = 8 + 4 * hero.pointsInTalent(Talent.SPIRIT_SHIELD) + 2 * hero.pointsInTalent(Talent.SPIRIT_VEIL);
		if (hero.subClass == HeroSubClass.SPIRIT_SHAMAN) shield += 6;
		Buff.affect(hero, Barrier.class).incShield(shield);
		int heal = hero.pointsInTalent(Talent.SPIRIT_LINK) + hero.pointsInTalent(Talent.SPIRIT_MEND);
		if (hero.subClass == HeroSubClass.SPIRIT_SHAMAN) heal += 2;
		if (heal > 0) hero.HP = Math.min(hero.HT, hero.HP + heal);
		if (hero.pointsInTalent(Talent.SPIRIT_ALLY) > 0 || hero.subClass == HeroSubClass.SPIRIT_SHAMAN) {
			NecroStaff.summon(hero, true);
		}
		Sample.INSTANCE.play(Assets.Sounds.READ);
		hero.sprite.operate(hero.pos);
		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		Invisibility.dispel();
		hero.spendAndNext(1f);
	}

	@Override
	public int icon() {
		return HeroIcon.SPIRIT_HAWK;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.SPIRIT_SHIELD, Talent.SPIRIT_ALLY, Talent.SPIRIT_LINK, Talent.HEROIC_ENERGY};
	}
}
