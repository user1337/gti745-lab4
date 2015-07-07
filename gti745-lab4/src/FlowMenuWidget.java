import java.awt.Color;


public class FlowMenuWidget extends CustomWidget {
	
	// The radial menu has a central menu item (with index 0)
	// and up to 8 surrounding menu items
	// (with indices 1 through 8, numbered clockwise,
	// with 1 for North, 2 for North-East, ..., 8 for North-West).

	public static final int CENTRAL_ITEM = 0;
	private static final int N = 8;

	// Each menu item has a corresponding ``label'' string.
	// If a given label string is empty (""),
	// then there is no menu item displayed for it.
	// In addition, the client can temporarily deactivate
	// an existing menu item by setting its ``isEnabled''
	// flag to false.
	private String [] label = new String[ N + 1 ];
	private boolean [] isEnabled = new boolean[ N + 1 ];

	// Each menu item also has a (normally distinct) ID number.
	// These are useful for causing multiple items to hilite together:
	// whenever the user drags over a given item,
	// it and all other items with the same ID hilite together.
	// This is intended for cases where there are redundant menu items
	// that map to the same function in the client's code.
	private int [] itemID = new int[ N + 1 ];

	private int selectedItem; // in the range [CENTRAL_ITEM,N]

	// pixel coordinates of center of menu
	protected int x0 = 0, y0 = 0;

	// pixel coordinates of current mouse position
	private int mouse_x, mouse_y;


	// These are in pixels.
	public static final int radiusOfNeutralZone = 10;
	public static final int textHeight = Constant.TEXT_HEIGHT;
	public static final int marginAroundText = Constant.MARGIN_AROUND_TEXT;
	public static final int marginBetweenItems = Constant.MARGIN_BETWEEN_ITEMS;

	public static final Color foregroundColor = new Color( 0, 0, 0 );
	public static final Color foregroundColor2 = new Color( 127, 127, 127 );
	public static final Color backgroundColor = new Color( 255, 255, 255 );

	
	
	public FlowMenuWidget() {
		for (int i = 0; i <= N; ++i) {
			label[i] = new String("");
			isEnabled[i] = true;

			// Give every item a distinct ID.
			itemID[i] = i;
		}
	}
	
	// Returns a status code.
	public int pressEvent( int x, int y ) {
		x0 = mouse_x = x;
		y0 = mouse_y = y;
		selectedItem = CENTRAL_ITEM;
		isVisible = true;
		return S_REDRAW;
	}
	
	public void setItemLabelAndID( int index, String s, int id ) {
		if ( 0 <= index && index <= N ) {
			label[index] = s;
			itemID[index] = id;
		}
	}
	
	// For internal use only.
	private boolean isItemHilited( int index ) {
		assert 0 <= index && index <= N;
		return itemID[ index ] == itemID[ selectedItem ];
	}
	
	protected void drawMenuItems(
			GraphicsWrapper gw,
			boolean drawOnlyHilitedItem, // if false, all menu items are drawn
			boolean drawUsingPieStyle,
			int radiusOfPie // only used if ``drawUsingPieStyle'' is true
		) {
			final float alpha = Constant.MENU_ALPHA;

			if ( drawUsingPieStyle ) {
				gw.setColor( foregroundColor2, alpha );
				gw.fillCircle(
					x0-radiusOfPie, y0-radiusOfPie,
					radiusOfPie
				);
			}

			if ( ! isItemHilited( CENTRAL_ITEM ) )
				gw.setColor( foregroundColor );
			else
				gw.setColor( backgroundColor );
			gw.drawCircle(
				x0-radiusOfNeutralZone, y0-radiusOfNeutralZone,
				radiusOfNeutralZone
			);
			
			
			
			
			
			
			//To change
			int heightOfItem = textHeight + 2*marginAroundText;
			float radius = 2*( heightOfItem + marginBetweenItems );
			float radiusPrime = radius / (float)Math.sqrt(2.0f);

			for ( int i = 1; i <= N; ++i ) {
				if ( label[i].length() > 0 && isEnabled[i] ) {
					float theta = (float)( (i-1)*Math.PI/4 - Math.PI/2 );
					// compute center of ith label
					float x = ( (i%2)==1 ? radius : radiusPrime ) * (float)Math.cos( theta ) + x0;
					float y = ( (i%2)==1 ? radius : radiusPrime ) * (float)Math.sin( theta ) + y0;

					if ( i == 1 && label[2].length() == 0 && label[8].length() == 0 ) {
						y = -radius/2 + y0;
					}
					else if ( i == 5 && label[4].length() == 0 && label[6].length() == 0 ) {
						y = radius/2 + y0;
					}

					float stringWidth = gw.stringWidth( label[i] );
					float widthOfItem = stringWidth + 2*marginAroundText;

					// We want items that appear side-by-side to have the same width,
					// so that the menu is symmetrical about a vertical axis.
					if ( i!=1 && i!=5 && label[N+2-i].length() > 0 ) {
						float otherStringWidth = gw.stringWidth( label[N+2-i] );
						if ( otherStringWidth > stringWidth )
							widthOfItem = otherStringWidth + 2*marginAroundText;
					}

					if ( 2 == i || 4 == i ) {
						if ( x - widthOfItem/2 <= x0 + marginBetweenItems )
							// item is too far to the left; shift it to the right
							x = x0 + marginBetweenItems + widthOfItem/2;
					}
					else if ( 3 == i ) {
						if ( x - widthOfItem/2 <= x0 + radiusOfNeutralZone + marginBetweenItems )
							// item is too far to the left; shift it to the right
							x = x0 + radiusOfNeutralZone + marginBetweenItems + widthOfItem/2;
					}
					else if ( 6 == i || 8 == i ) {
						if ( x + widthOfItem/2 >= x0 - marginBetweenItems )
							// item is too far to the right; shift it to the left
							x = x0 - marginBetweenItems - widthOfItem/2;
					}
					else if ( 7 == i ) {
						if ( x + widthOfItem/2 >= x0 - radiusOfNeutralZone - marginBetweenItems )
							// item is too far to the right; shift it to the left
							x = x0 - radiusOfNeutralZone - marginBetweenItems - widthOfItem/2;
					}

					if ( isItemHilited( i ) )
						gw.setColor( foregroundColor, alpha );
					else
						gw.setColor( backgroundColor, alpha );
					gw.fillRect(
						x - widthOfItem/2, y - heightOfItem/2,
						widthOfItem, heightOfItem
					);
					if ( ! isItemHilited( i ) )
						gw.setColor( foregroundColor );
					else
						gw.setColor( backgroundColor );
					gw.drawRect(
						x - widthOfItem/2, y - heightOfItem/2,
						widthOfItem, heightOfItem
					);
					gw.drawString(
						Math.round( x - stringWidth/2 ),
						Math.round( y + textHeight/2 ),
						label[i]
					);
				}
			}
			
			
			
		}

		public void draw(
			GraphicsWrapper gw
		) {
			if ( ! isVisible )
				return;

			drawMenuItems( gw, false, false, 0 );
		}
}
