package scot.davidhunter.twopointfivejavagame.net.packets;

import scot.davidhunter.twopointfivejavagame.net.GameClient;
import scot.davidhunter.twopointfivejavagame.net.GameServer;

public class Packet01Disconnect extends Packet
{
	private String username;
	
	public Packet01Disconnect( byte[] data )
	{
		super( 01 );
		
		this.username = readData( data );
	}
	
	public Packet01Disconnect( String username )
	{
		super( 01 );
		
		this.username = username;
	}
	
	@Override
	public void writeData( GameClient client )
	{
		if ( client != null )
			client.sendData( getData() );
	}
	
	@Override
	public void writeData( GameServer server )
	{
		server.sendDataToAllClients( getData() );
	}
	
	@Override
	public byte[] getData()
	{
		return ( "01" + this.username ).getBytes();
	}
	
	public String getUsername()
	{
		return username;
	}
}
