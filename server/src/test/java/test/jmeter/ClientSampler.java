package test.jmeter;

import java.io.IOException;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;

import com.jayway.jsonpath.internal.function.text.Concatenate;
import com.pbit.client.Client;
import com.pbit.client.nio.NioClient;

public class ClientSampler  extends AbstractJavaSamplerClient  {

	private Client client = null;
	private static final org.apache.log.Logger log = LoggingManager.getLoggerForClass();
	 
	public void setupTest (JavaSamplerContext context) { 
		
		String address = context.getParameter( "address" );
		String port = context.getParameter( "port" );
		
		
		client = new NioClient();
		try {
			client.open(address, Integer.parseInt(port));
			
			while(!client.isConnected()){
				Thread.sleep(500);
				System.out.println("wait--connected");
			}
			System.out.println("connected--->");
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.debug("client open");
	}
	public Arguments getDefaultParameters () {
		Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("address", "127.0.0.1");
        defaultParameters.addArgument("port", "5000");
        
		return defaultParameters;
		
	}
	public SampleResult runTest(JavaSamplerContext arg0) {
		SampleResult result = new SampleResult();
		result.setSuccessful(false);
		String res_srt = "00000";
		if(client !=null && client.isConnected()){
			try {
				String req_data = "SampleTest";
				client.request(req_data.getBytes());
				byte[] res_data = client.response();
				
				if(res_data != null){
					res_srt = new String(res_data);
					if(res_srt.equals(req_data)){
						result.setSuccessful(true);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		log.info("result_string : " + res_srt);
		return result;
	}
	public void teardownTest (JavaSamplerContext arg0) { 
		if(null!=client && client.isConnected()){
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		log.debug("client close");
	} 

}
