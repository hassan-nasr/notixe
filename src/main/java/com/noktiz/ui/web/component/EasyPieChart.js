/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function EasyPieChart(id, color) {
    $("#" + id).easyPieChart({
        animate: 1000,
        size: 110,
        lineWidth: 3,
        barColor: color
    });
}