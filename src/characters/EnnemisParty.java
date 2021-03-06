package characters;

import game.battle.IBattle;

import java.util.ArrayList;
import java.util.HashMap;

import util.Random;

/**
 * Repr�sente un groupe d'ennemis.
 * 
 * @author Darklev
 *
 */
public class EnnemisParty 
{
	private HashMap<Integer,Ennemy> ennemis;
	private int rarity;
	
	private String music;
	
	/**
	 * Constructeur d'ennemis.
	 * 
	 * @param rarity
	 * 		Indice de raret� pour la rencontre du groupe d'ennemis.
	 * @param music
	 * 		Nom de la musique � jouer lors du combat.
	 * @param ennemis
	 * 		Les ennemis du groupe.
	 */
	public EnnemisParty(int rarity, String music, HashMap<Integer,Ennemy> ennemis)
	{
		this.rarity = rarity;
		this.ennemis = ennemis;
		this.music = music;
	}
	
	/*
	public void ajouter(int place, Ennemi ennemi)
	{
		ennemis.put(place,ennemi);
	}
	 */
	/**
	 * Retourne les ennemis du groupe.
	 * 
	 * @return les ennemis du groupe.
	 */
	public HashMap<Integer, Ennemy> getEnnemis() 
	{
		return ennemis;
	}

	/**
	 * Retourne vrai si tous les ennemis ont une action pr�par�e.
	 * Renvoie faux sinon.
	 * 
	 * @return vrai si les ennemis ont une action pr�par�e, sinon faux.
	 */
	public boolean areReady() 
	{
		for(Ennemy e : ennemis.values())
		{
			if(!e.isReady())
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Retourne le nom de la musique.
	 * 
	 * @return le nom de la musique.
	 */
	public String getMusic()
	{
		return music;
	}
	
	/**
	 * Retourne vrai si la cible est vivante et existante, faux sinon.
	 * 
	 * @param i
	 * 		Index de l'ennemi � tester.
	 * @return
	 * 		vrai si la cible est vivante et existante, faux sinon.
	 */
	public boolean isValidTarget(int i) 
	{
		if(ennemis.containsKey(i))
		{
			if(ennemis.get(i).isAlive())
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Retourne le nombre d'ennemis vivants dans le groupe.
	 * 
	 * @return
	 * 
	 * 		Le nombre d'ennemis vivants dans le groupe.
	 */
	public int getNumberOfAliveEnnemis() 
	{
		int total = 0;
		for(Ennemy e : ennemis.values())
		{
			if(e.isAlive())
				total++;
		}
		return total;
	}

	/**
	 * Retourne le premier ennemi valide pour �tre cibl�.
	 * 
	 * @return le premier ennemi valide.
	 */
	public Ennemy getFirstValidTarget() {
		for(int e : ennemis.keySet()){
			if(isValidTarget(e)){
				return ennemis.get(e);
			}
		}
		return null;
	}

	/**
	 * Retourne l'index d'un ennemi ou -1 si l'ennemi n'est pas trouv�.
	 * 
	 * @param ennemy
	 * 		Ennemy dont l'index est � retourner.
	 * 	@return l'index de l'ennemi pass� en param�tre ou -1 si l'ennemi est introuvable.
	 */
	public int indexOf(Ennemy ennemy) {
		for(int i : ennemis.keySet()){
			if(ennemy == ennemis.get(i)){
				return i;
			}
		}
		return -1;
	}
	
	
	/**
	 * Retourne les ennemis pouvant �tre cibl�s.
	 * 
	 * @return les ennemis pouvant �tre cibl�s.
	 */
	public ArrayList<IBattle> getValidTargets(){
		
		ArrayList<IBattle> out = new ArrayList<IBattle>();
		
		for(Integer i : ennemis.keySet()){
			if(isValidTarget(i)){
				out.add(ennemis.get(i));
			}
		}
		
		return out;
	}

	/**
	 * Retourne un ennemi al�atoire pouvant �tre cibl�.
	 * 
	 * @return un ennemi al�atoire pouvant �tre cibl�.
	 */
	public IBattle getRandomTarget() {
		ArrayList<IBattle> valids = getValidTargets();
		
		return valids.get(Random.randInt(0, valids.size() - 1));
	}
}
