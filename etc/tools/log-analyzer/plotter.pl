reset
set term png truecolor
set offsets 5, 5, 5, 5
set xdata time
set timefmt '"%Y-%m-%d %H'
set xlabel "Timestamp"
set ylabel "# Exception"
set format x '%Y-%m-%d %H (h)'
set xtics rotate 
set output "error_report/exception_dashboard.png"
plot "error_report/statistics.dat" u 1:2 lc rgb "red" notitle
