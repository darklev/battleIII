package characters;


import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import bag.item.stuff.Weapon;
import bag.item.stuff.Armor;


/**
 * Personnage mage.
 * 
 * @author Sulivan
 *
 */
public class Mage extends Character 
{
	private int mana;
	
	/**
	 * Constructeur
	 * 
	 * @param name
	 * 		Nom du personnage.
	 */
	public Mage(String name) 
	{
		super(name);		
		statistics.put("endurance",10);
		statistics.put("force",5);
		statistics.put("dexterite",10);
		statistics.put("intelligence",20);
		statistics.put("sagesse",15);
		
		statisticsUP.put("endurance",3);
		statisticsUP.put("force",1);
		statisticsUP.put("dexterite",2);
		statisticsUP.put("intelligence",5);
		statisticsUP.put("sagesse",4);
		
		healthPoints=statistics.get("endurance")*5-10;
		
		mainWeaponTypes.add(Weapon.STAFF);
		
		armorTypes.add(Armor.FABRIC);
		
		mana = statistics.get("intelligence")*5;
		
		try 
		{
			sprites = new SpriteSheet("ressources/spritesheet/mage.png", 32,32,new Color(255,0,255));
			for(int i = 0; i<5; i++)
			{
				animations[i] = new Animation(sprites, 0, i, 2,i ,true, 100, true);
			}
		} catch (SlickException e) 
		{
			e.printStackTrace();
		}
	}


	@Override
	public int getEnergy() 
	{
		return mana;
	}

	@Override
	public int getMaximumEnergy()
	{
		return getMaximumStatistic("intelligence")*5 + getBonusEnergy();
	}
	



	@Override
	public void renderCharacterPanel(Graphics g, int x, int y, boolean selectionne) 
	{
		super.renderCharacterPanel(g, x, y, selectionne);
		super.renderCharacterPanel(g, x, y, selectionne);
		
		g.setColor(new Color(255,255,255));
		g.drawString("Mana", x+54, y+86);
		
		g.setColor(energyColor());
		g.fillRect(x+96, y+91, mana/getMaximumEnergy()*149, 9);
	}
	
	@Override
	public Color energyColor()
	{
		return new Color(0,100,150);
	}

	@Override
	public String getEnergyLabel() {
		return "Mana";
	}
	
}
