function promptText(id, value) {
  var field = $(id);

  if (field.value == '') {
    field.value = value;
    field.addClassName('prompt');
  }

  field.observe('focus', function(e) {
    if (field.value == value) {
      field.value = '';
      field.removeClassName('prompt');
    }
  });

  field.observe('blur', function(e) {
    if (field.value == '') {
      field.value = value;
      field.addClassName('prompt');
    }
  });

  if (field.form) {
    $(field.form).observe('submit', function(e) {
      if (field.value == value) {
        field.value = '';
      }
    });
  }
}
