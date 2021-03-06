package game;

import game.settings.Settings;
import game.system.application.Application;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import characters.Character;
import characters.Party;
import bag.Bag;
import bag.Category;
import bag.IItems;
import bag.item.stuff.Weapon;
import bag.item.stuff.Armor;
import bag.item.stuff.Stuff;
import util.Format;


/**
 * Game State permettant d'avoir un aper�u sur les objets du joueur 
 * et d'int�ragir avec (utilisation, jeter ...)
 * 
 * @author Darklev
 *
 */
public class InterfaceSac extends Top
{


	//#region -------CODES CATEGORIE OBJET-----------
	
	/*
	 * Cat�gories :
	 * - Tous : tous les objets 
	 * - Divers : ingr�dients ...
	 * - Combat : objet utilisable en combat (potions, antidotes ...)
	 * - Equipement : armes et armures
	 * - Rares : objets de qu�te.
	 */
	
	public static final int DIVERS = 0;
	public static final int EQUIPEMENT = 1;
	public static final int COMBAT = 2;
	public static final int RARE = 3;
	
	//#endregion

	//#region ------CODES ACTION---------------------
	
	public static final int UTILISER = 0;
	public static final int EQUIPER = 1;
	public static final int JETER = 2;
	public static final int DEPLACER = 3;
	public static final int DESEQUIPER = 4;
	
	//#endregion

	//#region -----CODES CONFIRMATION----------------
	
	private static final int  NON = 0;
	private static final int OUI = 1;
	
	//#endregion

	//#region ----------PROPRIETES-------------------
	private int curseurCategorie = 0; // curseur de cat�gorie
	private ArrayList<Integer> curseursAbsolus; //curseurs des objets de chaque cat�gories
	private ArrayList<Integer> curseursRelatifs;
	private int curseurAction;
	
	private Bag sac;
	private Party equipe;
	
	private boolean categorieValidee;
	private boolean confirmation = false; 
	private String message;

	private IItems objetSelectionne;
	private ArrayList<Integer> listeActions;
	private int actionSelectionnee = -1;
	
	private int curseurPersonnage = 0;
	private Character personnageSelectionne; 
	
	private int curseurDeplacement = 0;
	private int curseurConfirmation = 0;
	private int boiteComptage = 1;
	
	//#endregion
	
	//#region ----OVERRIDE BASICGAMESTATE------------
	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException 
	{
		equipe = Application.application().getGame().getParty();
		sac = equipe.getBag();
	
		categorieValidee = false;
		curseursAbsolus = new ArrayList<Integer>();
		curseursRelatifs = new ArrayList<Integer>();
		listeActions = new ArrayList<Integer>();
		
		for(int i = 0; i<5; i++)
			curseursAbsolus.add(0);
		for(int i = 0; i<5; i++)
			curseursRelatifs.add(0);
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException 
	{
		dessinerInterface(g);
		if(!categorieValidee)
		{
			//Affichage curseur cat�gorie
			g.drawImage(Application.application().getGame().getArrow(0), 37 + curseurCategorie*160, 55);
			if(getCategorie().size() != 0)
			{
				afficherListeObjets(g);
			}
		}
		else
		{
			afficherListeObjets(g);
			afficherCurseurObjets(g);
			if(getCategorie().size() != 0)
			{				
				if(objetSelectionne != null)
				{
					if(actionSelectionnee == -1)
					{
						afficherListeActions(g);
					}
					else
					{
						switch(actionSelectionnee)
						{
						case(JETER):
							if(getCategorie().getQuantity(curseursAbsolus.get(curseurCategorie)) > 1 && !confirmation)
							{
								afficherQuantiteAJeter(g);
							}
							else
							{
								afficherConfirmation(g);
							}
							break;
						case(UTILISER):
						case(EQUIPER):
							afficherEquipePourUtiliser(g);
							break;
						}
					}
				}
				else
				{
					afficherDecriptionObjet(g);
				}
			}
		}
		if(message != null && message != "")
		{
			afficherMessage(g);
		}
		
		super.render(container, game, g);
		
	}




	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException 
	{
		if(message == null || message == "")
		{
			if(!categorieValidee)
			{
				gererChoixCategorie(container.getInput());
				if(container.getInput().isKeyPressed(Input.KEY_ESCAPE))
				{
					game.enterState(StatesId.MENU);
				}
				container.getInput().clearKeyPressedRecord();
			}
			else if(actionSelectionnee == -1)
			{
				if(objetSelectionne == null)
				{
					gererChoixObjet(container.getInput());
				}
				else
				{
					gererChoixAction(container.getInput());
				}	
			}
			else
			{
				switch(actionSelectionnee)
				{
				case(JETER):
					if(getCategorie().getQuantity(curseursAbsolus.get(curseurCategorie)) > 1 && !confirmation)
					{
						gererQuantiteAJeter(container.getInput());
					}
					else
					{
						gererCurseurConfirmation(container.getInput());
					}
					break;
				case(EQUIPER):
					if(personnageSelectionne == null)
					{
						gererCurseurSelectionPersonnage(container.getInput());
					}
					else
					{
						if(personnageSelectionne.equipStuff((Stuff)objetSelectionne))
						{
							message = personnageSelectionne + " est �quip� de  "+objetSelectionne+".";
							actionSelectionnee = -1;
							objetSelectionne = null;
						}
						else
						{
							message = personnageSelectionne + " ne peut pas s'�quiper de "+objetSelectionne+".";
						}
						personnageSelectionne = null;
					}
					break;
				case(DESEQUIPER):
					if(personnageSelectionne == null)
					{
						message="D�s�quiper";
						actionSelectionnee = -1;
						Character perso = ((Stuff)objetSelectionne).whoIsEquipedOfThis(equipe);
						perso.desequipe((Stuff)objetSelectionne);
						objetSelectionne = null;
					}
					break;
				case(UTILISER):
					if(personnageSelectionne == null)
					{
						gererCurseurSelectionPersonnage(container.getInput());
					}
					else
					{
						if(personnageSelectionne.useItem(objetSelectionne))
						{
							sac.supprimer(objetSelectionne, 1);
							if(sac.getQuantity(objetSelectionne) == 0)
							{
								objetSelectionne = null;
							}
							actionSelectionnee = -1;
						}
						else
						{
							message = "Ca n'aura aucun effet!";

						}
						personnageSelectionne = null;
					}
					break;
				}
			}
		}
		else
		{
			if(container.getInput().isKeyPressed(Input.KEY_RETURN) || container.getInput().isKeyPressed(Input.KEY_ESCAPE))
			{
				message = null;
			}
		}
		
		super.update(container, game, delta);
		
		container.getInput().clearKeyPressedRecord();
	}





	@Override
	public int getID() 
	{
		return StatesId.SAC;
	}

	//#endregion

	//#region --------AFFICHAGE----------------------
	
	/**
	 * Dessine l'interface de la vue sac. 
	 * @param g
	 */
	private void dessinerInterface(Graphics g)
	{
		g.setColor(Settings.BACKGROUND_COLOR);
		g.fillRect(2, 2, 638, 478);
		
		g.setColor(Settings.BORDER_COLOR);
		g.drawRect(0, 0, 639, 479);
		g.drawRect(1, 1, 637, 477);
		g.drawLine(0, 40, 640, 40);
		g.drawLine(0, 41, 640, 41);
		
		g.drawLine(0, 390, 640, 390);
		g.drawLine(0, 389, 640, 389);
		
		for(int i = 0; i<4; i++)
		{
			g.drawRect(160*i,40, 160, 40);
			g.drawRect(160*i+1,41, 159, 40);
		}
		
		g.setColor(Color.white);
		g.drawString("SAC", 290,10);
		
		g.drawString("Divers", 50, 50);
		g.drawString("Equip.", 210, 50);
		g.drawString("Combat", 370, 50);
		g.drawString("Rares", 530, 50);
		
		g.drawString(Application.application().getGame().getParty().getMoney() + " PO" , 450, 10);
		
		
		
		//effet onglet (cache)
		g.setColor(Settings.BACKGROUND_COLOR);
		g.fillRect(2 + curseurCategorie * 160, 80, 158, 2);
	}
	
	/**
	 * Affiche le curseur de s�lection d'objet.
	 * 
	 * @param g
	 */
	private void afficherCurseurObjets(Graphics g) 
	{
		int index = curseursRelatifs.get(curseurCategorie);
		g.drawImage(Application.application().getGame().getArrow(0), 5, 93 + index * 25);
	}
	
	/**
	 * Affiche la descritption de l'objet en face du curseur.
	 * @param g
	 */
	private void afficherDecriptionObjet(Graphics g)
	{
		g.setColor(Color.white);
		g.drawString(Format.multiLines(getCategorie().getItem(curseursAbsolus.get(curseurCategorie)).getDescription(), 70), 5, 395);
	}
	
	
	private void afficherListeActions(Graphics g)
	{
		g.setColor(Color.white);
		

		for(int i = 0 ; i<listeActions.size(); i++)
		{
			g.drawString(libelleAction(listeActions.get(i)), 17, 395 + i * 20);
		}
		
		//affichage du curseur :
		g.drawImage(Application.application().getGame().getArrow(0), 5, 400 + curseurAction * 20);
	}
	
	/**
	 * 3
	 * 
	 * Affiche interface pour jeter un objet.
	 * (message + bo�te compteur)
	 * 
	 * @param g
	 */
	private void afficherQuantiteAJeter(Graphics g)
	{
		g.setColor(Color.white);
		g.drawString(Format.multiLines("Jeter combien de : " + getCategorie().getItem(curseursAbsolus.get(curseurCategorie)).getName() + " ?", 70), 5, 395);
	
		//affichage de la boite de comptage :
		{
			g.setColor(Settings.BORDER_COLOR);
			g.drawRect(280, 415, 80, 25);
			g.drawRect(279, 414, 82, 27);
			
			g.setColor(Color.white);
			g.drawString(String.valueOf(boiteComptage), 285, 420);
		}
	}
	
	/**
	 * 4
	 * 
	 * Affiche message de confirmation
	 * 
	 * @param g
	 */
	private void afficherConfirmation(Graphics g)
	{
		g.setColor(Color.white);
		g.drawString(Format.multiLines( getCategorie().getItem(curseursAbsolus.get(curseurCategorie)).getName()  + " : " + " en jeter " + boiteComptage +" ?", 70), 5, 395);
		
		g.drawString("Non", 100, 415);
		g.drawString("Oui", 180, 415);
		
		//desinerCurseur
		g.drawImage(Application.application().getGame().getArrow(0), 87 + 80 * curseurConfirmation, 418);
		
	}
	
	/**
	 * Affiche la liste des objets de la cat�gorie s�lectionn�e.
	 * 
	 * @param g
	 * @throws SlickException
	 */
	private void afficherListeObjets(Graphics g) throws SlickException
	{
			int index = 0;
			for(int i = curseursAbsolus.get(curseurCategorie) - curseursRelatifs.get(curseurCategorie);
					i<getCategorie().size() && index<12; i++)
			{
				
				g.drawImage(getCategorie().getItem(i).getIcon(), 20, 90 + 25*index);
				g.setColor(Color.white);
				g.drawString(getCategorie().getItem(i).getName(), 45 ,90 + 25*index);
				g.drawString("x"+getCategorie().getQuantity(i), 590 ,90 + 25*index);
				index++;
			}
	}
	
	/**
	 * Affiche les membres de l'�quipe pour pouvoir s�lectionner une personnage
	 * et a lui appliquer un objet.
	 * @param g
	 */
	private void afficherEquipePourUtiliser(Graphics g)
	{
		int i = 0;
		for(Character p : equipe)
		{
			p.renderCharacterPanel(g,128, i*160, false);
			i++;
		}
		
		Image fleche = Application.application().getGame().getArrow(0);
		g.drawImage(fleche, 130, 80 + curseurPersonnage*160);
		
	}
	
	/**
	 * Affiche le message.
	 * @param g
	 */
	private void afficherMessage(Graphics g) 
	{
		g.setColor(Color.white);
		g.drawString(Format.multiLines(message, 70), 5, 395);
	}
	
	
	//#endregion

	//#region ---------GESTION INPUT-----------------
	
	
	/**
	 * 0
	 * G�re le curseur du choix de la cat�gorie.
	 * 
	 * @param in
	 * 		Input
	 */
	private void gererChoixCategorie(Input in) 
	{

		if(in.isKeyPressed(Input.KEY_LEFT))
		{
			curseurCategorie = (curseurCategorie + 3) % 4;
		}
		
		
		else if(in.isKeyPressed(Input.KEY_RIGHT))
		{
			curseurCategorie = (curseurCategorie + 1) % 4;
		}
		else if(in.isKeyPressed(Input.KEY_RETURN))
		{
			categorieValidee = true;
		}
		
	}
	
	/**
	 * 1
	 * G�re le curseur pour le choix de l'objet
	 * @param in
	 */
	private void gererChoixObjet(Input in) 
	{
		if(in.isKeyPressed(Input.KEY_ESCAPE))
		{
			categorieValidee = false;
			in.clearKeyPressedRecord();
		}
		else if(in.isKeyPressed(Input.KEY_UP )  && getCategorie().size() != 0)
		{
			int indexA = curseursAbsolus.get(curseurCategorie);
			curseursAbsolus.set(curseurCategorie, (indexA - 1 + getCategorie().size()) % getCategorie().size());
			indexA = curseursAbsolus.get(curseurCategorie);
			int indexR = curseursRelatifs.get(curseurCategorie);
			
			
			if(indexA + 1 == getCategorie().size())
			{
				curseursRelatifs.set(curseurCategorie, Math.min(getCategorie().size()-1, 11));
			}
			else
			{
				curseursRelatifs.set(curseurCategorie, Math.max(0, indexR-1));
			}
		}
		else if(in.isKeyPressed(Input.KEY_DOWN)  && getCategorie().size() != 0)
		{
			int indexA = curseursAbsolus.get(curseurCategorie);
			curseursAbsolus.set(curseurCategorie, (indexA + 1) % getCategorie().size());
			indexA = curseursAbsolus.get(curseurCategorie);
			int indexR = curseursRelatifs.get(curseurCategorie);
			
			
			if(indexA == 0)
			{
				curseursRelatifs.set(curseurCategorie, 0);
			}
			else
			{
				curseursRelatifs.set(curseurCategorie, Math.min(indexR+1, 11));
			}
		}
		else if(in.isKeyPressed(Input.KEY_RETURN))
		{
			if(getCategorie().size() > 0)
			{
				objetSelectionne = getCategorie().getItem(curseursAbsolus.get(curseurCategorie));
				updateListeAtions();
			}
		}
	}
	
	/**
	 * 2
	 * G�re le choix de l'action pour l'objet s�lectionn�.
	 * 
	 * @param in
	 */
	private void gererChoixAction(Input in)
	{
		if(in.isKeyPressed(Input.KEY_DOWN))
		{
			curseurAction = (curseurAction + 1) % listeActions.size();
		}
		else if(in.isKeyPressed(Input.KEY_UP))
		{
			curseurAction = (curseurAction - 1 + listeActions.size()) % listeActions.size();
		}
		else if(in.isKeyPressed(Input.KEY_ENTER))
		{
			actionSelectionnee = listeActions.get(curseurAction);
		}
		else if(in.isKeyPressed(Input.KEY_ESCAPE))
		{
			objetSelectionne = null;
		}
	}
	
	/**
	 * 3
	 * 
	 * G�re quantit� d'objet � jeter.
	 * 
	 * @param in
	 */
	private void gererQuantiteAJeter(Input in)
	{
		if(in.isKeyPressed(Input.KEY_DOWN))
		{
			boiteComptage = Math.max(1, boiteComptage - 1);
		}
		else if(in.isKeyPressed(Input.KEY_LEFT))
		{
			boiteComptage = Math.max(1, boiteComptage - 10);
		}
		else if(in.isKeyPressed(Input.KEY_UP))
		{
			boiteComptage = Math.min(getCategorie().getQuantity(curseursAbsolus.get(curseurCategorie)), boiteComptage + 1);
		}
		else if(in.isKeyPressed(Input.KEY_RIGHT))
		{
			boiteComptage = Math.min(getCategorie().getQuantity(curseursAbsolus.get(curseurCategorie)), boiteComptage + 10);
		}
		else if(in.isKeyPressed(Input.KEY_ESCAPE))
		{
			actionSelectionnee = -1;
		}
		else if(in.isKeyPressed(Input.KEY_RETURN))
		{

			confirmation = true;
		}
	}
	
	/**
	 * Met � jour la liste d'actions en fonction de l'objet s�lectionn�.
	 */
	private void updateListeAtions()
	{
		listeActions.clear();
		listeActions.add(UTILISER);		
		if(objetSelectionne instanceof Weapon || objetSelectionne instanceof Armor)
		{
			Stuff equipable = (Stuff) objetSelectionne;
			if(equipe.isEquiped(equipable))
			{
				listeActions.set(0, DESEQUIPER);
			}
			else
			{
				listeActions.set(0, EQUIPER);
			}
		}
		listeActions.add(DEPLACER);
		if(!objetSelectionne.isRare())
		{
			listeActions.add(JETER);
		}
	}

	
	

	
	/**
	 * 4
	 * 
	 * G�re le curseur de confirmation pour la suppression d'objet.
	 * 
	 * @param in
	 */
	private void gererCurseurConfirmation(Input in)
	{
		if(in.isKeyPressed(Input.KEY_LEFT))
		{
			curseurConfirmation = NON;
		}
		else if(in.isKeyPressed(Input.KEY_RIGHT))
		{
			curseurConfirmation = OUI;
		}
		else if(in.isKeyPressed(Input.KEY_ESCAPE))
		{
			confirmation = false;
		}
		else if (in.isKeyPressed(Input.KEY_RETURN))
		{
			if(curseurConfirmation == OUI)
			{
				getCategorie().remove(objetSelectionne, boiteComptage);
				curseursAbsolus.set(curseurCategorie, Math.min(curseursAbsolus.get(curseurCategorie), getCategorie().size() - 1));
				
				actionSelectionnee = -1;
				objetSelectionne = null;
				boiteComptage = 1;
				confirmation = false;
				
				//correction curseur Relatif :
				
				if(curseursRelatifs.get(curseurCategorie) > getCategorie().size() - 1 && getCategorie().size() != 0)
				{
					curseursRelatifs.set(curseurCategorie, getCategorie().size() - 1);
				}
				
				if(getCategorie().size() == 0)
				{
					curseursAbsolus.set(curseurCategorie, 0);
					curseursRelatifs.set(curseurCategorie, 0);
					categorieValidee = false;
				}
			}
			
			confirmation = false;
			curseurConfirmation = NON;
			
			if(getCategorie().size() > 0)
			{
				if(getCategorie().getQuantity(curseursAbsolus.get(curseurCategorie)) == 1)
				{
					actionSelectionnee = -1;
				}
			}
		}
		
		in.clearKeyPressedRecord();
	}
	
	/**
	 * Permet de g�rer le curseur de s�lection d'un personnage.
	 */
	private void gererCurseurSelectionPersonnage(Input in)
	{
		if(in.isKeyPressed(Input.KEY_DOWN))
		{
			curseurPersonnage = (curseurPersonnage + 1) % equipe.numberOfCharacters();
		}
		else if(in.isKeyPressed(Input.KEY_UP))
		{
			curseurPersonnage = (curseurPersonnage + equipe.numberOfCharacters() - 1) % equipe.numberOfCharacters();
		}
		else if(in.isKeyPressed(Input.KEY_ESCAPE))
		{
			actionSelectionnee = -1;
		}
		else if(in.isKeyPressed(Input.KEY_RETURN))
		{
			personnageSelectionne = equipe.get(curseurPersonnage);
		}
	}
	//#endregion

	//#region ---------AUTRES METHODES---------------
	
	/**
	 * Retourne la cat�gorie en cours.
	 * 
	 * @return
	 *  	la cat�gorie en cours.
	 */
	private Category getCategorie()
	{
		switch(curseurCategorie)
		{
		case(DIVERS):
			return sac.getMiscellaneous();
		case(EQUIPEMENT):
			return sac.getStuff();
		case(COMBAT):
			return sac.getCombat();
		case(RARE):
			return sac.getRares();
		default:
			return null;
		}
	}
	
	//#endregion
	
	//#region --------METHODES STATIQUES-------------
	
	/**
	 * Retourne le libelle de l'action dont le code est pass� en parma�tre.
	 * 
	 * @param code
	 * 		Code de l'action dont le libell� est � retourner.
	 * @return
	 * 		Le libelle de l'action dont le code est pass� en parma�tre.
	 */
	private static String libelleAction(int code)
	{
		switch(code)
		{
		case(UTILISER):
			return "Utiliser";
		case(EQUIPER):
			return "Equiper";
		case(DEPLACER):
			return "D�placer";
		case(JETER):
			return "Jeter";
		case(DESEQUIPER):
			return "D�s�quiper";			
		default:
			return "";
		}
	}
	
	//#endregion
}
