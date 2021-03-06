var TA = {
  removeEditor: function(){
    (function($){
      $('#DOMWindowOverlay').remove();
      $('#DOMWindow').remove();
    })(jQuery);
  },
  hideEditor: function(){
    (function($){
      $('#DOMWindowOverlay').css({
        display: 'none'
      });
      $('#DOMWindow').css({
        display: 'none'
      });
    })(jQuery);
  },
  addFormParameter: function($form, pValue){
    var action = $form.attr("action");
    action += "/" + pValue;
    $form.attr({
      action: action
    });
  },
  RegisterGuestDetails: {
      submitGuest: function(){
      (function($){
       $("input[id^=submitGuestBTN]").click();
      })(jQuery);
    }
  }
}
