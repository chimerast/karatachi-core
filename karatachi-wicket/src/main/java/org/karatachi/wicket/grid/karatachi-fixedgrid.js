(function($) {
  $.fn.fixedgrid = function(options) {
    var settings = $.extend( {
      tl_id : this.attr("id") + "_tl",
      tr_id : this.attr("id") + "_tr",
      bl_id : this.attr("id") + "_bl",
      br_id : this.attr("id") + "_br"
    }, options);

    var dragging = false;
    var dragX, dragY;

    var tl = $("#" + settings.tl_id);
    var tr = $("#" + settings.tr_id);
    var bl = $("#" + settings.bl_id);
    var br = $("#" + settings.br_id);

    var updateScroll = function(event) {
      tr.scrollLeft(br.scrollLeft());
      bl.scrollTop(br.scrollTop());
      $(document).data(this.attr("id") + "_scrollLeft", br.scrollLeft());
      $(document).data(this.attr("id") + "_scrollTop", br.scrollTop());
    }.bind(this);

    var restoreScroll = function() {
      var scrollLeft = $(document).data(this.attr("id") + "_scrollLeft");
      var scrollTop = $(document).data(this.attr("id") + "_scrollTop");
      if (scrollLeft == undefined || scrollTop == undefined)
        return;
      tr.scrollLeft(scrollLeft);
      bl.scrollTop(scrollTop);
      br.scrollLeft(scrollLeft);
      br.scrollTop(scrollTop);
    }.bind(this);

    br.width(this.width() - bl.width());
    br.height(this.height() - tr.height());
    tr.width(br.width());
    bl.height(br.height());

    br.scroll(updateScroll);

    var func = function(event) {
      dragging = true;
      dragX = event.pageX;
      dragY = event.pageY;
      $(document.body).css("cursor", "move");
      event.preventDefault();
    }.bind(this);

    $("#" + tl.attr("id") + " table:first-child").mousedown(func);
    $("#" + tr.attr("id") + " table:first-child").mousedown(func);
    $("#" + bl.attr("id") + " table:first-child").mousedown(func);
    $("#" + br.attr("id") + " table:first-child").mousedown(func);

    $(document).mousemove(function(event) {
      if (!dragging)
        return;
      br.scrollLeft(br.scrollLeft() + dragX - event.pageX);
      br.scrollTop(br.scrollTop() + dragY - event.pageY);
      dragX = event.pageX;
      dragY = event.pageY;
      updateScroll(event);
      event.preventDefault();
    });

    $(document).mouseup(function(event) {
      dragging = false;
      $(document.body).css("cursor", "auto");
    });

    $(window).resize(function(event) {
      br.width(this.width() - bl.width());
      br.height(this.height() - tr.height());
      tr.width(br.width());
      bl.height(br.height());
    }.bind(this));

    restoreScroll();

    return this;
  };
})(jQuery);
