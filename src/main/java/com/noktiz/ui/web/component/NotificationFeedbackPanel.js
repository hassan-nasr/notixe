/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

function feedback(text,level,timoOutInMilis){
var myclass="gritter-"+level;
//myclass="gritter-inf";

$.gritter.add({
    // (string | mandatory) the heading of the notification
    title: '',
    // (string | mandatory) the text inside the notification
    text: text,
    // (string | optional) the image to display on the left
//                 image: './assets/img/avatar1.jpg',
    // (bool | optional) if you want it to fade out on its own or just sit there
    sticky: false,
    // (int | optional) the time you want it to be alive for before fading out
    time: timoOutInMilis,
    class_name:myclass
});
}