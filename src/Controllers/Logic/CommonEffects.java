package Controllers.Logic;

import javafx.scene.effect.ColorAdjust;


/**
 * This class is used as a static class to store constant static ColorAdjust effects that are used with the class ControllerManager
 * to change the colors of the nodes in the controllers, colors are used for things like hover effects or on mouse clicked effects and more...
 * 
 *  @author Bshara
 * */
public final class CommonEffects {
	
	public final static ColorAdjust REQUESTS_TABLE_ELEMENT_BLUE = new ColorAdjust(-0.78, 0.0, 0.55, 1.0); 
	public final static ColorAdjust REQUESTS_TABLE_ELEMENT_GRAY = new ColorAdjust(-0.86, 0.0, 0.32, -1.0); 
	public final static ColorAdjust REQUESTS_TABLE_ELEMENT_BLACK = new ColorAdjust(0.0, 0.0, -0.48, -1.0); 
	public final static ColorAdjust MENU_ELEMENT_ON_HOVER = new ColorAdjust(0, 0, 0.65, 0); 
	public final static ColorAdjust MENU_ELEMENT_IDLE = new ColorAdjust(0, 0, 0, 0);
	public final static ColorAdjust MENU_ELEMENT_PRESSED = new ColorAdjust(0, 0, 0.9, 0); 

	
	
	public final static ColorAdjust REQUEST_DETAILS_BUTTON_GREEN = new ColorAdjust(0.88, 0, -0.23, -0.09); 
	public final static ColorAdjust REQUEST_DETAILS_BUTTON_RED = new ColorAdjust(0, 0, 0, 0); 
	public final static ColorAdjust REQUEST_DETAILS_BUTTON_GRAY = new ColorAdjust(0, 0.0, 0.25, -1.0); 
	public final static ColorAdjust REQUEST_DETAILS_BUTTON_BLACK = new ColorAdjust(0.0, 0.0, -0.48, -1.0); 
	public final static ColorAdjust REQUEST_DETAILS_BUTTON_BLUE = new ColorAdjust(-0.78, 0.0, 0.55, 1.0); 

	public final static ColorAdjust LOGO_SELECT = new ColorAdjust(0.21, 0, 0.17, 0); 

}
