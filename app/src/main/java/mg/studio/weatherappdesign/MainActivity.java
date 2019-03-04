package mg.studio.weatherappdesign;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import java.io.FileNotFoundException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        renewWeather();


    }

    public void renewWeather(){
        ConnectivityManager connectivityManager=
                (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();

        boolean avalible ;
        if (networkInfo != null)avalible=networkInfo.isAvailable();
         else avalible=false;

        Log.d("TAG", String.valueOf(avalible));

        if(avalible){
            Toast.makeText(this,"the data is refreshing",Toast.LENGTH_LONG).show();
            new DownloadUpdate().execute();
        }
        else{
            Toast.makeText(this,"Internet error!",Toast.LENGTH_LONG).show();
        }
    }
    public void btnClick(View view) {

       renewWeather();
        //((TextView)findViewById(R.id.temperature_of_the_day)).setText("27");
    }


    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "http://api.openweathermap.org/data/2.5/forecast?q=chongqing&units=metric&appid=12b2817fbec86915a6e9b4dbbd3d9036";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    Log.d("TAG", line);
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String temperature) {
            //Update the temperature displayed

              int len=temperature.length();
              String lin=String.valueOf(len);
              Log.d("TAG1", lin);
              int i;
              String temp=null,date=null;
              int biaot=1,biaod=1;
              char[] shu;
              for (i=0;i<len;i++){
                  if (    biaot==1 &&
                          temperature.charAt(i)=='t'&&
                          temperature.charAt(i+1)=='e'&&
                          temperature.charAt(i+2)=='m'&&
                          temperature.charAt(i+3)=='p'&&
                          temperature.charAt(i+4)=='"')
                  {      int begini=i+6;
                         while(temperature.charAt(i)!=',')i++;
                         shu=new char[30];
                         temperature.getChars(begini,i,shu,0);
                         temp=String.valueOf(shu);
                      Log.d("TAG1", temp);
                         biaot=0;
                  }

                  if (    biaod==1 &&
                          temperature.charAt(i)=='d'&&
                          temperature.charAt(i+1)=='t'&&
                          temperature.charAt(i+2)=='_'&&
                          temperature.charAt(i+3)=='t'&&
                          temperature.charAt(i+4)=='x'&&
                          temperature.charAt(i+5)=='t'
                  )
                  {      int begini=i+9;
                      while(temperature.charAt(i)!=' ')i++;
                      shu=new char[20];
                      temperature.getChars(begini,i,shu,0);
                      date=String.valueOf(shu);
                      biaod=0;
                      Log.d("TAG1", date);
                  }

                  if (biaod==0&&biaot==0)break;
              }

            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(temp);
            ((TextView) findViewById(R.id.tv_date)).setText(date);
            Toast.makeText(getApplicationContext(),"Data has been updated",Toast.LENGTH_SHORT).show();
        }
    }
}
