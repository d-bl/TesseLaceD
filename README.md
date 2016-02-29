# [DEMO](https://d-bl.github.io/TessedLace/)
Diagrams with variable stitches and thread styles for patterns found by [TesseLace.com].
[TesseLace.com]: http://TesseLace.com

## How it's made

Step by step changed an [example] for [D3.js]:

- replaced the server side JSon with the hard-coded `sample.js`
- applied arrow heads and flattened them to colored line ends to emulate [color coded diagrams]
- made nodes transparent except for bobbins
- added properties to identify the links that are part of a single thread
- turned the links from lines to paths with a third node to add mid-markers for twist marks

[scala code] is compiled into `matrix-graphs.js` to generate alternatives for the `sample.js`. These alternatives are based on patterns found by [TesseLace.com].

[example]: http://bl.ocks.org/mbostock/4062045
[D3.js]: http://d3js.org/
[color coded diagrams]: https://en.wikipedia.org/w/index.php?title=Mesh_grounded_bobbin_lace&oldid=639789191#Worker_pair_versus_two_pair_per_pin
[scala code]: https://github.com/d-bl/TessedLace/tree/master/
[1]: http://web.uvic.ca/~vmi/papers/bridges2012.html
[2]: http://web.uvic.ca/~vmi/papers/jmm2014.html

## How to contribute

You may just improve the grammar on the demo-page or on this readme, improve the layout or fix a more technical issue.

Don't know about version control in general or GitHub in particular? No problem:
* just create a github [account](https://github.com)
* hit the fork button at the top of this page
* go to `https://github.com/YOURID/TessedLace/tree/gh-pages/`, don't forget to replace YOURID
* choose the file you want to change and hit the pencil to start editing
* save your changes and test with your own demo-page: `http://YOURID.github.io/TessedLace/`, again: don't forget to replace YOURID
* create a pull request at `https://github.com/YOURID/TessedLace/tree/gh-pages/` or drop a note
