(function(remark) {

function RemoteApi(slideshow, url, options) {

  var self = this
    , defaultOptions = {
        debug: false
      };

  options = options || defaultOptions;

  for (var key in defaultOptions) {
    if (typeof options[key] === 'undefined') {
        options[key] = defaultOptions[key];
    }
  }

  var callbacks = {
    connect: [],
    disconnect: []
  };

  self.on = function(event, callback) {
    if (!callbacks[event]) {
      throw new Error("Illegal event: " + event);
    }
    callbacks[event].push(callback);
  };

  function applyCallback(to) {

    to.connection.onconnect = function() {
      var length = callbacks.connect.length;
      for (var i = 0; i < length; i++) {
        callbacks.connect[i]();
      }
    };

    to.connection.ondisconnect = function() {
      var length = callbacks.disconnect.length;
      for (var i = 0; i < length; i++) {
        callbacks.disconnect[i]();
      }
    };
  }

  self.follow = function() {
    var follower = new Follower(slideshow, url, options);
    applyCallback(follower);
    self.on("connect", function() {
      follower.sync();
    });
    return follower;
  };

  self.control = function(passcode) {
    var controller =  new Controller(slideshow, url, passcode, options);
    applyCallback(controller);
    return controller;
  };

}

function Follower(slideshow, url, options) {
  var self = this;

  self.connection = new Connection(url + "follow", options);

  // ondefiance を無理やり起こすための変数
  // 最後に follow したindexと現在表示しようとしているindexを比較して
  // 異なる場合に ondefiance を起こす
  var latestFollowIndex = null;

  self.connection.onreceive = function(data) {
    if (data.follow_slide && self.followWhen()) {
      // 必ず gotoSlide の前に値を入れておく
      latestFollowIndex = data.follow_slide.index;
      slideshow.gotoSlide(data.follow_slide.index + 1);
    }
  };

  slideshow.on('showSlide', function(slide) {
    if (latestFollowIndex != slide.getSlideIndex()) {
      self.ondefiance();
    }
  });

  self.sync = function() {
    self.connection.send({
      "request": "latest_command"
    });
  };
}
Follower.prototype.ondefiance = function() {};
Follower.prototype.followWhen = function() { return true };

function Controller(slideshow, url, passcode, options) {
  var self = this;

  self.connection = new Connection(url + "control?passcode=" + passcode, options);

  slideshow.on('showSlide', function(slide) {
    self.connection.send({
      "goto_slide": {
        "index": slide.getSlideIndex()
      }
    });
  });

}

function Connection(url, options) {
  var self = this;

  var connection = new ReconnectingWebSocket(url, null, {
    debug: options.debug
  });

  self.send = function(obj) {
    connection.send(JSON.stringify(obj));
  };

  connection.onopen = function() {
    self.onconnect();
  };

  connection.onclose = function() {
    self.ondisconnect();
  };

  connection.onmessage = function(e) {
    self.onreceive(JSON.parse(e.data));
  };
}
Connection.prototype.onconnect    = function() {};
Connection.prototype.onreceive    = function(data) {};
Connection.prototype.ondisconnect = function() {};

remark.remote = function(slideshow, url, options) {
  return new RemoteApi(slideshow, url, options);
};

})(remark);
