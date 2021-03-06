package video;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import video.OutputLogs.ACTION;
//! \details Runs and manages FFMPEG execution
public class VideoClip implements Runnable
{
	private VideoProfile profile;
	private Properties settings;
	private InputFiles ifiles;
	private OutputFiles ofiles;
	private String filter;
	public VideoClip(String input,Properties value,String output, VideoProfile pro)
	{
		settings = value;
		ifiles = new InputFiles();
		ofiles = new OutputFiles();
		ifiles.addFile(input);
		ofiles.addFile(output);
		profile = pro;
		filter = null;
	}
	public void setFilter(String value)
	{
		filter = value;
	}
	private String getFilterCommand()
	{
		return "-vf \""+filter+"\"";
	}
	public VideoProfile getProfile()
	{
		return profile;
	}
	public ArrayList<String> getFiles()
	{
		return ifiles.getFileList();
	}
	public ArrayList<String> getOutputFiles()
	{
		return ofiles.getFileList();
	}
	/*!
	 * Function: run
	 * \details builds FFMPEG command and runs it
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		Process p;
		OutputLogs stdout = null, errors = null;
		List<String> commands = new ArrayList<String>();
		ProcessBuilder builder = new ProcessBuilder();
		//build command list
		commands.add(settings.getProperty("FFMPEG"));
		commands.add("-y");//TODO: add input seeking
		//commands.addAll(profile.getInputCommands());
		commands.addAll(ifiles.getCommand());
		commands.addAll(profile.getCommands());
		if(filter != null)
			commands.add(this.getFilterCommand());
		
		commands.addAll(ofiles.getCommand());
		
		builder.command(commands);
		builder.redirectErrorStream(true);
		try {
			p = builder.start();
			stdout = new OutputLogs(p.getInputStream(),ACTION.PRINT);
			errors = new OutputLogs(p.getErrorStream(),ACTION.NONE);
	
	//OutputStream stdin = p.getOutputStream();
			p.waitFor();
			errors.Done();
			stdout.Done();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
