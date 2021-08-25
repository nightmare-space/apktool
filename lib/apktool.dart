
import 'dart:async';

import 'package:flutter/services.dart';

class Apktool {
  static const MethodChannel _channel = MethodChannel('apktool');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
