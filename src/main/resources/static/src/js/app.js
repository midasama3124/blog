import 'bootstrap';
import '../sass/main.scss';
import './custom';

console.warn("Running js files...");

/* Truncate length of card title and text */
export function truncateText(selector, maxLength) {
    $(selector).text(function(index, oldText) {
        if (oldText.length > maxLength) {
            return oldText.substring(0, maxLength) + '...';
        }
        return oldText;
    });
}
truncateText('.card-title', 20);
truncateText('.card-text', 150);

/* Show password if checkbox input is checked */
// Gather our DOM references.
var password = document.querySelector( ".password" );
var pswToggle = document.querySelector( ".show-password" );

// NOTE: The "(input)" event doesn't work on checkboxes in Safari or IE. As such,
// I'm using the "(click)" event to make this works cross-browser.
pswToggle.addEventListener( "click", handleToggleClick, false );

// I handle the toggle click, changing the TYPE of password input.
function handleToggleClick( event ) {
    if ( this.checked ) {
        console.warn( "Change input 'type' to: text" );
        password.type = "text";
    } else {
        console.warn( "Change input 'type' to: password" );
        password.type = "password";
    }
}

/* Admin page */
$('#menu-action').click(function() {
  $('.sidebar').toggleClass('active');
  $('.main').toggleClass('active');
  $(this).toggleClass('active');

  if ($('.sidebar').hasClass('active')) {
    $(this).find('i').addClass('fa-close');
    $(this).find('i').removeClass('fa-bars');
  } else {
    $(this).find('i').addClass('fa-bars');
    $(this).find('i').removeClass('fa-close');
  }
});

// Add hover feedback on menu
$('#menu-action').hover(function() {
    $('.sidebar').toggleClass('hovered');
});