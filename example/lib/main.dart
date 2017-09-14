import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:save_wav/save_wav.dart';
import 'dart:typed_data';
import 'dart:async';

import 'package:mic_stream/mic_stream.dart';
void main() {
  runApp(new MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  List<StreamSubscription<dynamic>> _micStreamSubscription = <StreamSubscription<dynamic>>[];
  Uint16List _micAudioFragment= null;
  num _samples = 10000;
  num _clipCounter;

  num _counter = 0;
  List<int> _micClip = null;
  @override
  initState() {
    super.initState();
    _micClip = new List();
    _micAudioFragment = new Uint16List(0);
    _micStreamSubscription.add(micEvents.listen((MicEvent e){
      setState((){
        _micAudioFragment = e.audioData;
        if(_clipCounter < _samples) {
          _micClip.addAll(_micAudioFragment);
          _clipCounter++;
        }
      });
    }));
  }

  @override
  void dispose() {
    super.dispose();
    for (var subscription in _micStreamSubscription) {
      subscription.cancel();
    }
  }
  _saveClip() async {
    try {
      await SaveWav.saveWavUInt16('${_counter}.wav', _micClip, 48000, 1);
    } on PlatformException {
      
    }
    if (!mounted)
      return;

    setState(() {
      _counter++;
      _micClip.clear();
    });
  }
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: new Text('Plugin example app'),
        ),
        body: new Center(
          child: new Text('Mic fragment size: ${_micClip.length}\n File saved: ${_counter}'),
        ),
        floatingActionButton: new FloatingActionButton(
          onPressed: _saveClip,
          tooltip: 'save to wav',
          child: new Icon(Icons.save),
        ),
      ),
    );
  }
}
