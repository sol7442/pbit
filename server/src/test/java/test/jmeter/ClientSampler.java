package test.jmeter;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;

import com.jayway.jsonpath.internal.function.text.Concatenate;
import com.pbit.client.Client;
import com.pbit.client.nio.NioClient;
import org.slf4j.Logger;

public class ClientSampler  extends AbstractJavaSamplerClient  {

	private Client client = null;
	//private static final org.apache.log.Logger log = LoggingManager.getLoggerForClass();
	
	//private static final Logger log = LoggerFactory.getlo.getLoggerForClass();
	
	public void setupTest (JavaSamplerContext context) { 
		
		String address = context.getParameter( "address" );
		String port = context.getParameter( "port" );
		
		
		client = new NioClient();
		try {
			client.connect(address, Integer.parseInt(port));
			
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
		getLogger().info("client open");
	}
	public Arguments getDefaultParameters () {
		Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("address", "127.0.0.1");
        defaultParameters.addArgument("port", "5000");
        
		return defaultParameters;
		
	}
	public SampleResult runTest(JavaSamplerContext arg0) {
		SampleResult result = new SampleResult();
		String res_srt = "00000";
		if(client !=null && client.isConnected()){
			try {
				result.setRequestHeaders("request..");
				String req_data = "result";
				byte[] res_data = client.request(req_data.getBytes());
				getLogger().info("result_string1 : " + new String(res_data));
				
				if(res_data != null){
					res_srt = new String(res_data);
					if(res_srt.equals(req_data)){
						result.setSuccessful(true);
						result.setResponseData(res_data);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
		}
		getLogger().info("result_string2 : " + res_srt);
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
		getLogger().info("client close");
	} 

}
