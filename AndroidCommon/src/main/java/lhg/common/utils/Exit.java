package lhg.common.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * 
 * 返回键按2次，退出
 */

public class Exit {
	private boolean isExit = false;

	private Runnable task = new Runnable() {  
		
		public void run() {  
			
			isExit = false;  	
		}  

	}; 
	
 

	public void doExitInOneSecond() {  

		isExit = true;
		new Handler(Looper.getMainLooper()).postDelayed(task, 5000);
		 
	}  

	public boolean isExit() {  
		return isExit;  	
	}  
	public void setExit(boolean isExit) {  
		this.isExit = isExit;  
	}  


}
