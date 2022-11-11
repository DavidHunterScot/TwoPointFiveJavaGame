package scot.davidhunter.twopointfivejavagame.entities;

import scot.davidhunter.twopointfivejavagame.gfx.Screen;
import scot.davidhunter.twopointfivejavagame.level.Level;

public abstract class Entity
{
	public int x, y;
	protected Level level;
	
	public Entity( Level level )
	{
		init( level );
	}
	
	public final void init( Level level )
	{
		
	}
	
	public abstract void tick();
	
	public abstract void render( Screen screen );
}
