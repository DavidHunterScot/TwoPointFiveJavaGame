package scot.davidhunter.twopointfivejavagame.level.tiles;

public class AnimatedTile extends BasicTile
{
	private int[][] animationTileCoords;
	private int currentAnimationIndex;
	private long lastIterationTime;
	private int animationSwitchDelay;
	
	public AnimatedTile( int id, int size, int[][] animationCoords, int tileColour, int levelColour, int animationSwitchDelay )
	{
		super( id, size, animationCoords[ 0 ][ 0 ], animationCoords[ 0 ][ 1 ], tileColour, levelColour );
		
		this.animationTileCoords = animationCoords;
		this.lastIterationTime = System.currentTimeMillis();
		this.animationSwitchDelay = animationSwitchDelay;
	}
	
	public void tick()
	{
		if ( ( System.currentTimeMillis() - lastIterationTime ) >= ( animationSwitchDelay ) )
		{
			lastIterationTime = System.currentTimeMillis();
			currentAnimationIndex = ( currentAnimationIndex + 1 ) % animationTileCoords.length;
			this.tileId = ( animationTileCoords[ currentAnimationIndex ][ 0 ] + ( animationTileCoords[ currentAnimationIndex ][ 1 ] * 32 ) );
		}
	}
}
