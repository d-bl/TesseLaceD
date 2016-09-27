# [DEMO](https://d-bl.github.io/GroundForge/)

A web based toolbox to design bobbin lace grounds with matching diagrams.


## How it's Made / Under the Hood

### Proof of concept with D3.js

To get a proof of concept a force graph [example] with originally v3 of [D3.js] was changed into tiny thread an pair diagrams diagrams with the following steps:

- Replaced the server side JSon with the hard-coded [js/sample.js] assembled from a manual sketch [js/sample.png].
- Applied arrow heads and flattened them to line ends to emulate [color coded pair diagrams] or to emulate the over/under effect in thread diagrams. Later versions create the over/under effect with shortened lines, for better browser support and performance, a too complicated approach for the multiple colors of a color coded diagram.
- Made nodes transparent except for bobbins.
- Assigned the thread number as a class to each section of a thread to assign colors.
- Turned the links from lines to paths with a third node to add mid-markers for twist marks.
- Initial coordinates replace the default random values, thus the animation stabalizes much quicker which prevents rotated and flipped diagrams.

[example]: http://bl.ocks.org/mbostock/4062045
[D3.js]: http://d3js.org/
[js/sample.js]: https://github.com/d-bl/GroundForge/blob/7a94b670636a138b1f417c0640561bfb1cbc5fc7/js/sample.js
[js/sample.png]: https://github.com/d-bl/GroundForge/blob/50421a210ee28c73bcdddbc997802d48128ce6b9/js/sample.png
[color coded pair diagrams]: https://en.wikipedia.org/w/index.php?title=Mesh_grounded_bobbin_lace&oldid=639789191#Worker_pair_versus_two_pair_per_pin


### Using data from TesseLace

To provide alternatives for the `js/sample.js` above, [scala code] transforms a selection of matrices generated by [TesseLace.com].
GroundForge uses a [compact] matrix format using one character defining the composition of incoming pairs to tag each node.

The geometric information within the matrices is used to initialise the thread diagrams, speeding up the animation as explained above.
These [pages] were used to create the thumbnails.
The diagrams lack the original geometric information after completion of the animation,
so topological duplicates were removed from the generated thumbnails.
Downloadable pattern sheets provide geometric variations that can be customised into intermediate and other variations.

The thread diagrams are not generated from the matrices,
but from the generated JSon data for pair diagrams by replacing nodes with stitches.
To keep track of the threads while constructing the diagram, 
the algorithm has to figure out a working order to create the lace just like a real lace maker does.

[pages]: https://github.com/d-bl/GroundForge/blob/master/src/test/resources/
[compact]: https://d-bl.github.io/GroundForge/images/legend.png
[scala code]: https://github.com/d-bl/GroundForge/tree/master/
[TesseLace.com]: http://TesseLace.com


### Color-picker: jscolor

Safari nor Internet Explorer support `<input type="color">`. The free [color-picker](http://jscolor.com/) works on both platforms and was easy to apply.


## How to Contribute

Even just improving some grammar is a welcomed contribution. 
When signed in with a github [account](https://github.com)
you will see edit buttons when browsing through the wiki pages of the user guide.
The procedure for the html pages listed above is less simple, select one to get the pencil that starts an edit.
This will cause github to fork the project under your own account and github guides you through further steps.

For suggestions or discussions, use the issues link on top of this page.