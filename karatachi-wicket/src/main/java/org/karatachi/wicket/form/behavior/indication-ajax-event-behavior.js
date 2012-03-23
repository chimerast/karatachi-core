function showIndicator(field, indicator) {
  var f = $(field);
  var i = $(indicator);
  var p = Position.cumulativeOffset(f);

  i.style.left = p[0] + 1 + 'px';
  i.style.top = p[1] + 1 + 'px';
  i.style.width = f.clientWidth + 'px';
  i.style.height = f.clientHeight + 'px';

  i.show();
}

function hideIndicator(indicator) {
  $(indicator).hide();
}
