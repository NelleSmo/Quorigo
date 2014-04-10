package com.example.quori;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

@SuppressLint("ShowToast")
public class Registration_Activity extends Activity implements OnClickListener {

	  	
	    private Button submit;
	    private Bitmap bmp;
	    private ImageView iv;
	    private static final int cameraData = 1;
	 

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_student_registration);  
	        submit = (Button)findViewById(R.id.registerbtn);
	        submit.setOnClickListener(this);
	    //	iv = new ImageView(null);
	    }
	   

	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.main, menu);
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle action bar item clicks here. The action bar will
	        // automatically handle clicks on the Home/Up button, so long
	        // as you specify a parent activity in AndroidManifest.xml.
	        int id = item.getItemId();
	        if (id == R.id.action_settings) {
	            return true;
	        }
	        return super.onOptionsItemSelected(item);
	    }

	    public void onClick(View view) {
	        switch(view.getId()){
	            case R.id.registerbtn:	
	            	//Intent intent = new Intent(this, Temp_Class.class);
	    			startActivity(intent);
	    			break;
	    			
	    			default:
	    				break;
	        }
	    }
	            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	            	//This variable below is what we can use to pass to the json parser which will be used in the database if we like
	            	Uri uriSavedImage = Uri.fromFile(new File("/sdcard/flashCropped.png"));
	            //	intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
	            	//startActivityForResult(intent, cameraData);          	
	        //  default:
	         //       break;
	       // }
	   //};
	   
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data){
	    	super.onActivityResult(requestCode, resultCode, data);
	    	if(resultCode == RESULT_OK){
	    		Bundle extras = data.getExtras();
	    		bmp = (Bitmap)extras.get("data");
	    		iv.setImageBitmap(bmp);
	    		Toast.makeText(this, "Image saved:"+ data.getData(), Toast.LENGTH_LONG).show();
	    	} else if(resultCode == RESULT_CANCELED){
	    		
	    	} else {
	    		
	    	}
	    }
}

