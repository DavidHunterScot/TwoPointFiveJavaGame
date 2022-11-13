package scot.davidhunter.twopointfivejavagame.level.tiles;

import scot.davidhunter.twopointfivejavagame.gfx.Colours;
import scot.davidhunter.twopointfivejavagame.gfx.Screen;
import scot.davidhunter.twopointfivejavagame.level.Level;

public abstract class Tile
{
	public static final Tile[] tiles = new Tile[ 256 ];
	
	public static final int VOID_ID = 0;
	public static final int STONE_ID = 1;
	public static final int GRASS_ID = 2;
	public static final int WATER_ID = 3;
	
	public static final int SIZE = 8;
	
	public static final Tile VOID = new BasicSolidTile( VOID_ID, SIZE, 0, 0, Colours.get( 000, -1, -1, -1 ), 0xff000000 );
	public static final Tile STONE = new BasicSolidTile( STONE_ID, SIZE, 1, 0, Colours.get( -1, 333, -1, -1 ), 0xff555555 );
	public static final Tile GRASS = new BasicTile( GRASS_ID, SIZE, 2, 0, Colours.get( -1, 131, 141, -1 ), 0xff00ff00 );
	public static final Tile WATER = new AnimatedTile( WATER_ID, SIZE, new int[][] { { 0, 5 }, { 1, 5 }, { 2, 5 }, { 1, 5 } }, Colours.get( -1, 004, 115, -1 ), 0xff0000ff, 1000 );
	
	protected byte id;
	protected int size;
	protected boolean solid;
	protected boolean emitter;
	private int levelColour;
	
	public Tile( int id, int size, boolean isSolid, boolean isEmitter, int levelColour )
	{
		if ( tiles[ id ] != null )
			throw new RuntimeException( "Duplicate tile id on: " + id );
		
		this.id = (byte) id;
		this.size = size;
		this.solid = isSolid;
		this.emitter = isEmitter;
		this.levelColour = levelColour;
		tiles[ id ] = this;
	}
	
	public byte getId()
	{
		return id;
	}
	
	public int getSize()
	{
		return size;
	}
	
	public boolean isSolid()
	{
		return solid;
	}
	
	public boolean isEmitter()
	{
		return emitter;
	}
	
	public int getLevelColour()
	{
		return levelColour;
	}
	
	public abstract void tick();
	
	public abstract void render( Screen screen, Level level, int x, int y );
}
