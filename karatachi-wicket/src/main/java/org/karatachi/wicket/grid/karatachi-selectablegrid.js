(function($) {
  $.fn.fixedgrid = function(options) {
    var settings = $.extend({
      tl_id : this.attr("id") + "_tl",
      tr_id : this.attr("id") + "_tr",
      bl_id : this.attr("id") + "_bl",
      br_id : this.attr("id") + "_br",
      menu_id : this.attr("id") + "_menu",
      selected_color : "#e0e0ff",
      isTableEmpty : function() {
        return this.attr("id") == undefined || this.attr("id") == null;
      },
      align : 0
    }, options);

    var self = this;
    if (settings.isTableEmpty.call(self)) {
      return;
    }
    var tl = $("#" + settings.tl_id);
    var tr = $("#" + settings.tr_id);
    var bl = $("#" + settings.bl_id);
    var br = $("#" + settings.br_id);
    var menu = $("#" + settings.menu_id);
    var w = $(window);
    var selectedColor = settings.selected_color;

    /*
     * スクロール処理
     */
    var updateScroll = function(event) {
      tr.scrollLeft(br.scrollLeft());
      bl.scrollTop(br.scrollTop());
      $(document).data(self.attr("id") + "_scrollLeft", br.scrollLeft());
      $(document).data(self.attr("id") + "_scrollTop", br.scrollTop());
    };

    var restoreScroll = function() {
      var scrollLeft = $(document).data(self.attr("id") + "_scrollLeft");
      var scrollTop = $(document).data(self.attr("id") + "_scrollTop");
      if (scrollLeft == undefined || scrollTop == undefined) {
        scrollLeft = br.find("td").eq(settings.align).prop("offsetLeft")
            - br.prop("clientWidth");
        if (scrollLeft < 0)
          scrollLeft = 0;
        scrollTop = 0;
      }
      tr.scrollLeft(scrollLeft);
      bl.scrollTop(scrollTop);
      br.scrollLeft(scrollLeft);
      br.scrollTop(scrollTop);
    };

    var resizeGrid = function() {
      var cellWidth = Math.max(bl.width(), tl.width());
      var cellHeight = Math.max(tr.height(), tl.height());
      
      var rightWidth = self.width() - cellWidth;
      var bottomHeight = self.height() - cellHeight;
      br.width(rightWidth);
      br.height(bottomHeight);
      tr.width(rightWidth);
      bl.height(bottomHeight);
    };

    var isIE6 = function() {
      if ($.browser.msie) {
        var version = parseInt($.browser.version, 10);
        return version == 6;
      }
      return false;
    }

    if (!isIE6()) {
      w.resize(resizeGrid);
    }
    br.scroll(updateScroll);

    resizeGrid();
    restoreScroll();

    /*
     * セルの生成
     */
    var cells = function() {
      var tl_table = tl.find("table.inner")[0];
      var tr_table = tr.find("table.inner")[0];
      var bl_table = bl.find("table.inner")[0];
      var br_table = br.find("table.inner")[0];

      var rows = (tl_table.rows.length - 1)
          + (Math.max(bl_table.rows.length - 1, 0));
      var cols = (tl_table.rows[0].cells.length - 1)
          + (tr_table.rows[0].cells.length - 1);

      var cells = new Array(rows);
      for ( var i = 0; i < rows; ++i) {
        cells[i] = new Array(cols);
      }

      var setup = function(td, r, c, type) {
        var cell = $(td);
        td.$ = cell;

        cell.row = r;
        cell.col = c;
        cell.type = type;
        cell.backgroundColor = cell.css("background-color");

        var colSpan = cell.prop("colSpan");
        var rowSpan = cell.prop("rowSpan");
        if (colSpan != 1) {
          cell.colSpan = colSpan;
          cell.rowSpan = 1;
          for ( var k = 0; k < colSpan; ++k, ++c) {
            cells[r][c] = cell;
          }
        } else if (cell.rowSpan != 1) {
          cell.colSpan = 1;
          cell.rowSpan = rowSpan;
          for ( var k = 0; k < rowSpan; ++k, ++r) {
            cells[r][c] = cell;
          }
        } else {
          cell.colSpan = 1;
          cell.rowSpan = 1;
          cells[r][c] = cell;
        }
      };

      var r = 0;
      for ( var i = 0, max = tl_table.rows.length - 1; i < max; ++i) {
        var c = 0;
        for ( var j = 0, max2 = tl_table.rows[i].cells.length - 1; j < max2; ++j) {
          while (cells[r][c] != null) {
            ++c;
          }
          setup(tl_table.rows[i].cells[j], r, c, "tl");
        }
        for ( var j = 0, max2 = tr_table.rows[i].cells.length - 1; j < max2; ++j) {
          while (cells[r][c] != null) {
            ++c;
          }
          setup(tr_table.rows[i].cells[j], r, c, "tr");
        }
        ++r;
      }
      for ( var i = 0, max = bl_table.rows.length - 1; i < max; ++i) {
        var c = 0;
        for ( var j = 0, max2 = bl_table.rows[i].cells.length - 1; j < max2; ++j) {
          while (cells[r][c] != null) {
            ++c;
          }
          setup(bl_table.rows[i].cells[j], r, c, "bl");
        }
        for ( var j = 0, max2 = br_table.rows[i].cells.length - 1; j < max2; ++j) {
          while (cells[r][c] != null) {
            ++c;
          }
          setup(br_table.rows[i].cells[j], r, c, "br");
        }
        ++r;
      }

      cells.get = function(r, c) {
        if (r < 0 || r >= cells.length) {
          return null;
        } else if (c < 0 || c >= cells[0].length) {
          return null;
        } else {
          return cells[r][c];
        }
      };

      return cells;
    }();

    /*
     * セルの選択
     */
    var selecting = false;
    var shiftDown = false;
    var start = null;
    var end = null;
    var selected = {};

    var getSelected = function() {
      if (start == null || end == null) {
        return null;
      }
      var top, bottom, left, right;
      if (start.row < end.row) {
        top = start.row;
        bottom = end.row + end.rowSpan - 1;
      } else {
        top = end.row;
        bottom = start.row + start.rowSpan - 1;
      }
      if (start.col < end.col) {
        left = start.col;
        right = end.col + end.colSpan - 1;
      } else {
        left = end.col;
        right = start.col + start.colSpan - 1;
      }
      for ( var r = top, max = bottom; r <= max; ++r) {
        for ( var c = left, max2 = right; c <= max2; ++c) {
          var cell = cells.get(r, c);
          if (cell.row < top) {
            top = cell.row;
          }
          if (cell.row + cell.rowSpan - 1 > bottom) {
            bottom = cell.row + cell.rowSpan - 1;
          }
          if (cell.col < left) {
            left = cell.col;
          }
          if (cell.col + cell.colSpan - 1 > right) {
            right = cell.col + cell.colSpan - 1;
          }
        }
      }
      return {
        top : top,
        left : left,
        bottom : bottom,
        right : right
      };
    };

    var clearSelected = function() {
      for ( var key in seleted) {
        var cell = selected[key];
        cell.css("background-color", cell.backgroundColor);
      }
    };

    var updateSelected = function() {
      var area = getSelected();
      if (area == null) {
        return false;
      }
      var newSelected = {};
      for ( var r = area.top, max = area.bottom; r <= max; ++r) {
        for ( var c = area.left, max2 = area.right; c <= max2; ++c) {
          var cell = cells.get(r, c);
          if (cell.row == r && cell.col == c) {
            if (selected[r + "_" + c] === undefined) {
              cell.css("background-color", selectedColor);
            }
            newSelected[r + "_" + c] = cell;
          }
        }
      }
      for ( var key in selected) {
        if (newSelected[key] === undefined) {
          var cell = selected[key];
          cell.css("background-color", cell.backgroundColor);
        }
      }
      selected = newSelected;
      return true;
    };

    var getSelectedText = function() {
      var text = "";
      var area = getSelected();
      if (area == null) {
        return text;
      }
      for ( var r = area.top, max = area.bottom; r <= max; ++r) {
        for ( var c = area.left, max2 = area.right; c <= max2; ++c) {
          var cell = cells.get(r, c);
          if (cell.row == r && cell.col == c) {
            text += $.trim(cell.text()) + "\t";
          } else {
            text += "\t";
          }
        }
        if (text.length > 0) {
          text = text.substring(0, text.length - 1);
        }
        text += "\n";
      }
      if (text.length > 0) {
        text = text.substring(0, text.length - 1);
      }
      return text;
    };

    self.on({
      "mousedown" : function(event) {
        var cell = this.$;
        switch (event.which) {
        case 3:
          menu.css({
            position : "fixed",
            left : event.clientX,
            top : event.clientY
          });
          menu.show();
          break;
        default:
          selecting = true;
          $(document.body).css("cursor", "pointer");
          if (shiftDown) {
            end = cell;
          } else {
            start = end = cell;
          }
          updateSelected();
          menu.hide();
          break;
        }
        event.preventDefault();
      },
      "mousemove" : function(event) {
        var cell = this.$;
        if (selecting) {
          if (end != cell) {
            end = cell;
            updateSelected();
          }
        }
      }
    }, "table.inner td");

    $(document).on({
      "keydown" : function(event) {
        if (event.which == 16) {
          shiftDown = true;
        }
      },
      "keyup" : function(event) {
        if (event.which == 16) {
          shiftDown = false;
        }
      },
      "mouseup" : function(event) {
        if (event.which != 3) {
          selecting = false;
          $(document.body).css("cursor", "auto");
        }
      }
    });

    // IE7のリーク対策
    $(window).unload(function() {
      self.off();
      $(document).off();
      $(window).off();
    });

    /*
     * スクロール
     */

    var brrect = null;

    var reculcRect = function() {
      var offset = br.offset();
      brrect = {
        top : offset.top,
        left : offset.left,
        bottom : offset.top + br.height(),
        right : offset.left + br.width()
      };
    };

    var windowRect = function() {
      var top = w.scrollTop();
      var left = w.scrollLeft();
      return {
        top : top,
        left : left,
        bottom : top + w.height(),
        right : left + w.width()
      };
    };

    if (!isIE6()) {
      w.resize(reculcRect);
    }
    reculcRect();

    var scroll = null;
    var mouse = {};
    var perviousTime = 0;

    var callback = function() {
      var scrolled = false;
      var updated = false;

      if (selecting) {
        var brscroll = {};
        var duration = (new Date().getTime() - previousTime);
        if (duration > 50) {
          duration = 50;
        }
        var delta = duration / 20.0;

        if (mouse.pageY < brrect.top) {
          br.scrollTop(br.scrollTop() + (mouse.pageY - brrect.top) * delta);
          if (mouse.pageY < tl.offset().top) {
            if (end != cells.get(0, end.col)) {
              end = cells.get(0, end.col);
              updated = true;
            }
          }
          scrolled = true;
        }
        if (mouse.pageY > brrect.bottom) {
          br.scrollTop(br.scrollTop() + (mouse.pageY - brrect.bottom) * delta);
          if (end != cells.get(cells.length - 1, end.col)) {
            end = cells.get(cells.length - 1, end.col);
            updated = true;
          }
          scrolled = true;
        }
        if (mouse.pageX < brrect.left) {
          br.scrollLeft(br.scrollLeft() + (mouse.pageX - brrect.left) * delta);
          if (mouse.pageX < tl.offset().left) {
            if (end != cells.get(end.row, 0)) {
              end = cells.get(end.row, 0);
              updated = true;
            }
          }
          scrolled = true;
        }
        if (mouse.pageX > brrect.right) {
          br.scrollLeft(br.scrollLeft() + (mouse.pageX - brrect.right) * delta);
          if (end != cells.get(end.row, cells[0].length - 1)) {
            end = cells.get(end.row, cells[0].length - 1);
            updated = true;
          }
          scrolled = true;
        }

        var top, left;
        var wrect = windowRect();
        if (mouse.pageY < wrect.top) {
          top = wrect.top + (mouse.pageY - wrect.top) * delta;
          if (top < bl.offset().top + bl.height() - w.height())
            top = bl.offset().top + bl.height() - w.height();
          w.scrollTop(top);
          scrolled = true;
        }
        if (mouse.pageY > wrect.bottom) {
          top = wrect.top + (mouse.pageY - wrect.bottom) * delta;
          if (top > tl.offset().top)
            top = tl.offset().top;
          w.scrollTop(top);
          scrolled = true;
        }
        if (mouse.pageX < wrect.left) {
          left = wrect.left + (mouse.pageX - wrect.left) * delta;
          if (left < tr.offset().left + tr.width() - w.width())
            left = tr.offset().left + tr.width() - w.width();
          w.scrollLeft(left);
          scrolled = true;
        }
        if (mouse.pageX > wrect.right) {
          left = wrect.left + (mouse.pageX - wrect.right) * delta;
          if (left > tl.offset().left)
            left = tl.offset().left;
          w.scrollLeft(left);
          scrolled = true;
        }
      }

      if (updated) {
        updateSelected();
      }

      if (scrolled) {
        updateScroll();
        previousTime = new Date().getTime();
        scroll = setTimeout(callback, 0);
      } else {
        scroll = null;
      }
    };

    $(document).mousemove(
        function(event) {
          mouse.pageX = event.pageX;
          mouse.pageY = event.pageY;

          if (scroll == null) {
            var wrect = windowRect();
            if (mouse.pageY < brrect.top || mouse.pageY > brrect.bottom
                || mouse.pageX < brrect.left || mouse.pageX > brrect.right
                || mouse.pageY < wrect.top || mouse.pageY > wrect.bottom
                || mouse.pageX < wrect.left || mouse.pageX > wrect.right) {
              previousTime = new Date().getTime();
              scroll = setTimeout(callback, 0);
            }
          }
        });

    menu.show = function() {
    };

    menu.hide = function() {
      menu.css({
        left : "-9999px",
        top : "-9999px"
      });
    };

    self.on("contextmenu", false);

    if ($.browser.msie) {
      self.disableTextSelect();
    }

    /*
     * クリップボード
     */
    var playerVersion = swfobject.getFlashPlayerVersion();
    if (playerVersion.major >= 9
        && !($.browser.msie && $.browser.version == "6.0")) {
      ZeroClipboard.setMoviePath(settings.zclip_swf);
      var clip = new ZeroClipboard.Client();

      var copy = menu.find(".copy");

      clip.setHandCursor(true);
      clip.addEventListener("mouseover", function(client) {
        copy.addClass("hover");
      });
      clip.addEventListener("mouseout", function(client) {
        copy.removeClass("hover");
      });
      clip.addEventListener("mousedown", function(client) {
        copy.addClass("active");
        copy.removeClass("hover");
        clip.setText(getSelectedText());
      });
      clip.addEventListener("complete", function(client, text) {
        menu.hide();
        copy.removeClass("active");
      });

      var container = "<div></div>";
      container = $(container).css({
        position : "relative",
        marginBottom : -20,
        width : 120,
        height : 20,
        zIndex : 10000
      });
      copy.before(container);

      menu.find("div")[0].innerHTML = clip.getHTML(120, 20);
    } else if ($.browser.msie) {
      var copy = menu.find(".copy");
      copy.mouseover(function(client) {
        copy.addClass("hover");
      });
      copy.mouseout(function(client) {
        copy.removeClass("hover");
      });
      copy.mousedown(function(client) {
        copy.addClass("active");
        copy.removeClass("hover");
        clipboardData.setData("Text", getSelectedText());
        menu.hide();
        copy.removeClass("active");
      });
    } else {
      menu.show = function() {
        menu.hide();
        alert("選択したセルのコピーには、\nAdobe Flashバージョン9以降のインストールが必要です。");
      };
    }

    return this;
  };
})(jQuery);
