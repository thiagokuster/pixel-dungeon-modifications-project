package com.shatteredpixel.shatteredpixeldungeon.items.weapon;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Random;

public class DualWield {

	private DualWield() {}

	public static boolean usingBoth(Hero hero) {
		KindOfWeapon main = hero.belongings.weapon();
		KindOfWeapon off = hero.belongings.offhand();
		return main instanceof MeleeWeapon
				&& off instanceof MeleeWeapon
				&& ((MeleeWeapon) main).grip() != MeleeWeapon.Grip.TWO_HANDED
				&& ((MeleeWeapon) off).grip() != MeleeWeapon.Grip.TWO_HANDED;
	}

	public static boolean primaryMeleeSwing(Hero hero) {
		if (hero.belongings.thrownWeapon != null || hero.belongings.abilityWeapon != null) return false;
		if (!usingBoth(hero)) return false;
		return hero.belongings.attackingWeapon() == hero.belongings.weapon();
	}

	public static int combinedStr(Hero hero) {
		Weapon main = (Weapon) hero.belongings.weapon();
		Weapon off = (Weapon) hero.belongings.offhand();
		return main.STRReq() + (off.STRReq() + 1) / 2;
	}

	public static void followUp(Hero hero, Char enemy) {
		if (!hero.isAlive() || !enemy.isAlive() || enemy.isInvulnerable(hero.getClass())) return;
		MeleeWeapon off = (MeleeWeapon) hero.belongings.offhand();
		KindOfWeapon main = hero.belongings.weapon();

		float accMulti = 0.8f;
		if (main != null) {
			float mainAcc = Math.max(0.05f, main.accuracyFactor(hero, enemy));
			accMulti *= off.accuracyFactor(hero, enemy) / mainAcc;
		}

		if (!Char.hit(hero, enemy, accMulti, false)) {
			if (enemy.sprite != null) {
				enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
			}
			return;
		}

		int damage = Math.round(off.damageRoll(hero) * off.grip().offhandDamage);
		if (Random.Float() < 0.5f) {
			if (off instanceof MagesStaff) {
				if (off.enchantment != null && enemy.isAlive()) {
					damage = off.enchantment.proc(off, hero, enemy, damage);
				}
			} else if (enemy.isAlive()) {
				damage = off.proc(hero, enemy, damage);
			}
		}

		if (!enemy.isAlive()) return;
		int dr = Math.round(enemy.drRoll() * AscensionChallenge.statModifier(enemy));
		int effective = Math.max(damage - dr, 0);
		enemy.damage(effective, hero);
		off.hitSound(Random.Float(0.87f, 1.15f));
	}
}
