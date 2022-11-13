package scot.davidhunter.twopointfivejavagame.level.tiles;

public class BasicSolidTile extends BasicTile
{
	
	public BasicSolidTile( int id, int size, int x, int y, int tileColour, int levelColour )
	{
		super( id, size, x, y, tileColour, levelColour );
		
		this.solid = true;
	}
	
}
