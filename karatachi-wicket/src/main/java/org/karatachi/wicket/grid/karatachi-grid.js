var KaratachiGrid = Class.create();

KaratachiGrid.prototype = {
  initialize : function(owner, id, fixedheader, rowheader, colheader) {
    this.parent = $(owner);
    this.container = $(id);
    this.fixedcontainer = $(fixedheader);
    this.rowcontainer = $(rowheader);
    this.colcontainer = $(colheader);
    this.dragging = false;

    this.container.style.width = (this.parent.clientWidth - this.rowcontainer.offsetWidth) + 'px';
    this.container.style.height = (this.parent.clientHeight - this.colcontainer.offsetHeight) + 'px';

    this.colcontainer.style.width = this.container.style.width;
    this.rowcontainer.style.height = this.container.style.height;

    var func = function(e) {
      this.dragging = true;
      this.x = e.pointerX();
      this.y = e.pointerY();
      document.body.style.cursor = 'move';
      e.stop();
    }.bind(this);

    if (this.container.firstChild.tagName) {
      $(this.container.firstChild).observe('mousedown', func);
      $(this.rowcontainer.firstChild).observe('mousedown', func);
      $(this.colcontainer.firstChild).observe('mousedown', func);
    } else {
      $(this.container.firstChild.nextSibling).observe('mousedown', func);
      $(this.rowcontainer.firstChild.nextSibling).observe('mousedown', func);
      $(this.colcontainer.firstChild.nextSibling).observe('mousedown', func);
    }

    this.container.observe('scroll', function(e) {
      this.colcontainer.scrollLeft = this.container.scrollLeft;
      this.rowcontainer.scrollTop = this.container.scrollTop;
    }.bind(this));

    document.observe('mousemove', function(e) {
      if (this.dragging) {
        this.container.scrollLeft += this.x - e.pointerX();
        this.container.scrollTop += this.y - e.pointerY();
        this.colcontainer.scrollLeft = this.container.scrollLeft;
        this.rowcontainer.scrollTop = this.container.scrollTop;
        this.x = e.pointerX();
        this.y = e.pointerY();
        e.stop();
      }
    }.bind(this));

    document.observe('mouseup', function(e) {
      this.dragging = false;
      document.body.style.cursor = 'auto';
    }.bind(this));

    var resize = function(e) {
      this.container.style.width = (this.parent.clientWidth - this.rowcontainer.offsetWidth) + 'px';
      this.container.style.height = (this.parent.clientHeight - this.colcontainer.offsetHeight) + 'px';

      this.colcontainer.style.width = this.container.style.width;
      this.rowcontainer.style.height = this.container.style.height;
    }.bind(this);

    var ie6 = false;
    if (Prototype.Browser.IE) {
      if (typeof document.body.style.maxHeight == "undefined") {
        ie6 = true;
      }
    }

    if (!ie6) {
      Event.observe(window, 'resize', resize);
    }
  },

  saveScroll : function() {
    return [ this.container.scrollLeft, this.container.scrollTop ];
  },

  restoreScroll : function(pos) {
    this.container.scrollLeft = pos[0];
    this.container.scrollTop = pos[1];
    this.colcontainer.scrollLeft = this.container.scrollLeft;
    this.rowcontainer.scrollTop = this.container.scrollTop;
  }
};
