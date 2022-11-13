package scot.davidhunter.twopointfivejavagame;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import scot.davidhunter.twopointfivejavagame.entities.Player;
import scot.davidhunter.twopointfivejavagame.entities.PlayerMP;
import scot.davidhunter.twopointfivejavagame.gfx.Screen;
import scot.davidhunter.twopointfivejavagame.gfx.SpriteSheet;
import scot.davidhunter.twopointfivejavagame.level.Level;
import scot.davidhunter.twopointfivejavagame.net.GameClient;
import scot.davidhunter.twopointfivejavagame.net.GameServer;
import scot.davidhunter.twopointfivejavagame.net.packets.Packet00Login;

public class Game extends Canvas implements Runnable
{
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 160;
	public static final int HEIGHT = WIDTH / 12 * 9;
	public static final int SCALE = 3;
	public static final String NAME = "Two Point Five Java Game";
	public static final Dimension DIMENSIONS = new Dimension( WIDTH * SCALE, HEIGHT * SCALE );
	
	public static Game game;
	
	public JFrame frame;
	
	private Thread thread;
	
	public boolean running = false;
	public int tickCount = 0;
	
	private BufferedImage image = new BufferedImage( WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB );
	private int[] pixels = ( (DataBufferInt) image.getRaster().getDataBuffer() ).getData();
	private int[] colours = new int[ 6 * 6 * 6 ];
	
	private Screen screen;
	public InputHandler input;
	public WindowHandler windowHandler;
	public Level level;
	public Player player;
	
	public GameClient socketClient;
	public GameServer socketServer;
	
	public boolean debug = true;
	
	private String hostname;
	
	public void init()
	{
		game = this;
		
		int index = 0;
		for ( int r = 0; r < 6; r++ )
		{
			for ( int g = 0; g < 6; g++ )
			{
				for ( int b = 0; b < 6; b++ )
				{
					int rr = ( r * 255 / 5 );
					int gg = ( g * 255 / 5 );
					int bb = ( b * 255 / 5 );
					
					colours[ index++ ] = rr << 16 | gg << 8 | bb;
				}
			}
		}
		
		screen = new Screen( WIDTH, HEIGHT, new SpriteSheet( "/sprite_sheet.png" ) );
		input = new InputHandler( this );
		level = new Level( "/levels/water_test_level.png" );
		
		player = new PlayerMP( level, 100, 100, input, JOptionPane.showInputDialog( this, "Please enter a username." ), null, -1 );
		Packet00Login loginPacket = new Packet00Login( player.getUsername(), player.x, player.y );
		if ( socketServer != null )
			socketServer.addConnection( (PlayerMP) player, loginPacket );
		loginPacket.writeData( socketClient );
		
		level.addEntity( player );
		
		requestFocus();
	}
	
	public synchronized void start()
	{
		running = true;
		
		int port = 1331;
		
		if ( JOptionPane.showConfirmDialog( this, "Do you want to run the server?" ) == 0 )
		{
			String requestedPort = JOptionPane.showInputDialog( "Which port number would you like the server to listen on?" );
			
			try
			{
				port = Integer.parseInt( requestedPort );
			}
			catch ( NumberFormatException e )
			{
				
			}
			
			JOptionPane.showMessageDialog( this, "Server now running and listening on port number " + port + "..." );
			
			socketServer = new GameServer( this, port );
			socketServer.start();
			
			socketClient = new GameClient( this, "localhost", port );
			socketClient.start();
		}
		else
		{
			hostname = JOptionPane.showInputDialog( "Enter the hostname:port for multiplayer or cancel for singleplayer." );
			if ( hostname != null && ! hostname.trim().equals( "" ) )
			{
				if ( hostname.trim().contains( ":" ) )
				{
					String[] hostnameParts = hostname.trim().split( ":" );
					socketClient = new GameClient( this, hostnameParts[ 0 ], Integer.parseInt( hostnameParts[ 1 ] ) );
				}
				else
				{
					socketClient = new GameClient( this, hostname.trim(), port );
				}
				socketClient.start();
			}
		}
		
		thread = new Thread( this, NAME + "_main" );
		thread.start();
	}
	
	public synchronized void stop()
	{
		running = false;
		
		try
		{
			thread.join();
		}
		catch ( InterruptedException e )
		{
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D / 60D;
		
		int ticks = 0;
		int frames = 0;
		
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		
		init();
		
		while ( running )
		{
			long now = System.nanoTime();
			delta += ( now - lastTime ) / nsPerTick;
			lastTime = now;
			boolean shouldRender = true;
			
			while ( delta >= 1 )
			{
				ticks++;
				tick();
				delta -= 1;
				shouldRender = true;
			}
			
			try
			{
				Thread.sleep( 2 );
			}
			catch ( InterruptedException e )
			{
				e.printStackTrace();
			}
			
			if ( shouldRender )
			{
				frames++;
				render();
			}
			
			if ( System.currentTimeMillis() - lastTimer >= 1000 )
			{
				lastTimer += 1000;
				debug( DebugLevel.INFO, ticks + " UPS, " + frames + " FPS" );
				frames = 0;
				ticks = 0;
			}
		}
	}
	
	public void tick()
	{
		tickCount++;
		
		level.tick();
	}
	
	public void render()
	{
		BufferStrategy bs = getBufferStrategy();
		
		if ( bs == null )
		{
			createBufferStrategy( 3 );
			return;
		}
		
		int xOffset = player.x - ( screen.width / 2 );
		int yOffset = player.y - ( screen.height / 2 );
		
		level.renderTiles( screen, xOffset, yOffset );
		
		level.renderEntities( screen );
		
		for ( int y = 0; y < screen.height; y++ )
		{
			for ( int x = 0; x < screen.width; x++ )
			{
				int colourCode = screen.pixels[ x + y * screen.width ];
				
				if ( colourCode < 255 )
					pixels[ x + y * WIDTH ] = colours[ colourCode ];
			}
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.drawImage( image, 0, 0, getWidth(), getHeight(), null );
		
		g.dispose();
		bs.show();
	}
	
	public void debug( DebugLevel level, String msg )
	{
		switch ( level )
		{
			default:
			case INFO:
				if ( debug )
				{
					System.out.println( "[" + NAME + "] " + msg );
				}
				break;
			case WARNING:
				System.out.println( "[" + NAME + "] [WARNING] " + msg );
				break;
			case SEVERE:
				System.out.println( "[" + NAME + "] [SEVERE] " + msg );
				this.stop();
				break;
		}
	}
	
	public static enum DebugLevel
	{
		INFO, WARNING, SEVERE;
	}
}
