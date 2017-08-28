import 'dart:async';

import 'package:flutter/services.dart';

class SaveWav {
  static const MethodChannel _channel =
      const MethodChannel('save_wav');

  static Future<String> get platformVersion =>
      _channel.invokeMethod('getPlatformVersion');
}
