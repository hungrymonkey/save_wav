package com.yourcompany.savewav;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import android.util.Log;
import android.os.Environment;

import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * SaveWavPlugin
 */
public class SaveWavPlugin implements MethodCallHandler {
  /**
   * Plugin registration.
   */
  private static final String LOG_TAG = "save pcm";
  private static final String WAV_CHANNEL = "com.yourcompany.micstream/save_wav";
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), WAV_CHANNEL);
    channel.setMethodCallHandler(new SaveWavPlugin());
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("wav")) {
      HashMap<String, Object> args = (HashMap) call.arguments();
      byte [] audioData = (byte []) args.get("audioData");
      int channels = (int) args.get("channel");
      int sampleRate = (int) args.get("sampleRate");
      int bits = (int) args.get("bits");
      String fileName = (String ) args.get("filename");
      saveWav(fileName, audioData, sampleRate, bits, channels);
      result.sucess(1);
    } else {
      result.notImplemented();
    }
  }
  private void saveWav( String outF, byte [] data, long longSampleRate, int bitPerSample, int channels) {
    byte [] header = createWavHeader(data.length + 44 - 8,  data.length, longSampleRate,
      bitPerSample, channels);
    FileOutputStream outputStream = null;
    try {
      String extState = Environment.getExternalStorageState();
      String externalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
      File file = new File(externalStorage + File.separator + outF);
      file.createNewFile();
      os = new FileOutputStream(file);
      os.write(header);
      os.write(data);
      os.close();
    }catch(Exception ex) {
      log.e(LOG_TAG, "Error saving " + outF);
      ex.printStackTrace();
    }

  }
  //http://soundfile.sapp.org/doc/WaveFormat/
  //https://blogs.msdn.microsoft.com/dawate/2009/06/23/intro-to-audio-programming-part-2-demystifying-the-wav-format/
  //http://www.topherlee.com/software/pcm-tut-wavformat.html
  //http://www-mmsp.ece.mcgill.ca/Documents/AudioFormats/WAVE/WAVE.html
  private byte[] createWavHeader( long totalDataLen,  long totalAudioLen, long longSampleRate, int bitPerSample, int channels){
    byte[] header = new byte[44];
    long byteRate  = bitPerSample * channels * longSampleRate / 8;
    header[0] = 'R'; // RIFFWAVE header
    header[1] = 'I';
    header[2] = 'F';
    header[3] = 'F';
    header[4] = (byte) (totalDataLen & 0xff);
    header[5] = (byte) ((totalDataLen >> 8) & 0xff);
    header[6] = (byte) ((totalDataLen >> 16) & 0xff);
    header[7] = (byte) ((totalDataLen >> 24) & 0xff);
    header[8] = 'W';
    header[9] = 'A';
    header[10] = 'V';
    header[11] = 'E';
    header[12] = 'f'; // 'fmt ' chunk
    header[13] = 'm';
    header[14] = 't';
    header[15] = ' ';
    header[16] = 16; // 4 bytes: size of 'fmt ' chunk
    header[17] = 0;
    header[18] = 0;
    header[19] = 0;
    header[20] = 1; // format = 1
    header[21] = 0;
    header[22] = (byte) channels;
    header[23] = 0;
    header[24] = (byte) (longSampleRate & 0xff);
    header[25] = (byte) ((longSampleRate >> 8) & 0xff);
    header[26] = (byte) ((longSampleRate >> 16) & 0xff);
    header[27] = (byte) ((longSampleRate >> 24) & 0xff);
    header[28] = (byte) (byteRate & 0xff);
    header[29] = (byte) ((byteRate >> 8) & 0xff);
    header[30] = (byte) ((byteRate >> 16) & 0xff);
    header[31] = (byte) ((byteRate >> 24) & 0xff);
    header[32] = (byte) (bitPerSample * channels/8); // block align
    header[33] = 0;
    header[34] = (byte) bitPerSample; // bits per sample
    header[35] = 0;
    header[36] = 'd';
    header[37] = 'a';
    header[38] = 't';
    header[39] = 'a';
    header[40] = (byte) (totalAudioLen & 0xff);
    header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
    header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
    header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

    return header;
  }
}
