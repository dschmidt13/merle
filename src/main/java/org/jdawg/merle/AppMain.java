package org.jdawg.merle;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * AppMain launches the JavaFX app.
 * 
 * @author David Schmidt (dschmidt13@gmail.com)
 */
public class AppMain extends Application
{
	// Class constants.
	private static final URL MAIN_UI_FXML_DOC = AppMain.class.getResource( "Main.fxml" );
	private static final String WINDOW_TITLE = "Merle Generator";

	// Data members.
	private MainController fieldMainController;


	public static void main( String[ ] args )
	{
		launch( args );

	} // main


	@Override
	public void start( Stage primaryStage )
	{
		try
			{
			FXMLLoader loader = new FXMLLoader( MAIN_UI_FXML_DOC );
			Parent uiRoot = loader.load( );
			fieldMainController = loader.getController( );

			Scene scene = new Scene( uiRoot );
			primaryStage.setScene( scene );

			primaryStage.setTitle( WINDOW_TITLE );

			primaryStage.show( );
			}
		catch ( Exception exception )
			{
			System.err.println( "Exception launching app: " + exception.toString( ) );
			}

	} // start


	@Override
	public void stop( )
			throws Exception
	{
		if ( fieldMainController != null )
			{
			try
				{
				fieldMainController.destroy( );
				}
			catch ( Exception exception )
				{
				// TODO Auto-generated catch block
				exception.printStackTrace( );
				}
			}

		super.stop( );

	} // stop

}
