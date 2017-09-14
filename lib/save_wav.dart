import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';

class SaveWav {
  static const MethodChannel _channel =
      const MethodChannel('com.yourcompany.micstream/save_wav');

  static Future<Null> saveWavUInt16( String fileName,
      Uint16List audioData, num sampleRate, num channels) =>
      _channel.invokeMethod('wav', <String, dynamic> {
        "audioData" : audioData,
        "sampleRate" : sampleRate,
        "channel" : channels,
        "bits" : 16,
        "filename" : fileName,
      });
}
