import 'bootstrap';
import '../sass/main.scss';
import './custom';

// Truncate length of card title and text
export function truncateText(selector, maxLength) {
    $(selector).text(function(index, oldText) {
        if (oldText.length > maxLength) {
            return oldText.substring(0, maxLength) + '...';
        }
        return oldText;
    });
}truncateText('.card-title', 20);
truncateText('.card-text', 150);