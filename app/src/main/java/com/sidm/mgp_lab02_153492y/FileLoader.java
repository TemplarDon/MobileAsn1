package com.sidm.mgp_lab02_153492y;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Shania on 2/2/2017.
 */

public class FileLoader{

    FileOutputStream outputStream;

    public String readFromFile(Context context)
    {
        String ret = "";

        try
        {
            /*InputStream inputStream = context.getResources().openRawResource(R.raw.highscore);

            if ( inputStream != null )
            {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null )
                {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
            */

            InputStream in = context.openFileInput("highscore");
            if (in != null)
            {
                InputStreamReader tmp = new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(tmp);
                String str;
                StringBuffer buf = new StringBuffer();
                while ((str = reader.readLine()) != null)
                {
                    buf.append(str + ",");
                }
                in.close();
                return buf.toString();
            }

        }
        catch (NullPointerException e)
        {
            System.out.println("ERRRRRORR");
            e.printStackTrace();
        }
        catch(Exception e) {
            System.out.println("Unexcepted Exception");
            e.printStackTrace();
        }
        finally
        {
        }
        return ret;
    }

    public void writeToFile(String data, Context context) {
        try
        {
            /*
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("highscore.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            ou
            tputStreamWriter.close();
            */

            outputStream = context.openFileOutput("highscore", context.MODE_APPEND);
            outputStream.write(data.getBytes());
            outputStream.close();

        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
