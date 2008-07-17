
<p class="navbar"><a href="http://www.w3.org/"><img alt="W3C" width="72"
height="48" border="0" src="http://www.w3.org/Icons/w3c_home.gif"></a><br>
<a href="Advanced.html">Advanced HTML</a> | <a href="Style.html">Adding a
touch of style</a></p>

<h1><a href="http://www.w3.org/People/Raggett"><img src="dsr.jpg"
alt="Dave Raggett" align="middle" border="0"></a>
&nbsp;&nbsp;Getting started with HTML</h1>

<p><em><a href="http://www.w3.org/People/Raggett">Dave Raggett</a>,
revised 24 May 2005.</em></p>

<p>This is a short introduction to writing HTML. What is HTML? It is a
special kind of text document that is used by Web browsers to present
text and graphics. The text includes markup tags such as &lt;p&gt; to
indicate the start of a paragraph, and &lt;/p&gt; to indicate the end
of a paragraph. HTML documents are often refered to as "Web pages".
The browser retrieves Web pages from Web servers that thanks to the
Internet, can be pretty much anywhere in World.</p>

<p>Many people still write HTML by hand using tools such as NotePad
on Windows, or TextEdit on the Mac. This guide will get you up and
running. Even if you don't intend to edit HTML directly and instead
plan to use an HTML editor such as Netscape Composer, or W3C's Amaya,
this guide will enable you to understand enough to make better use of
such tools and how to make your HTML documents accessible on a wide range
of browsers. Once you are comfortable with the basics of authoring HTML,
you may want to learn how to <a href="Style.html"> add a touch of style</a>
using CSS, and to go on to try out features covered in my page on
<a href="Advanced.html">advanced HTML</a></p>

<p><i>p.s.</i> a good way to learn is to look at how other people have coded
their html pages. To do this, click on the "View" menu and then on "Source".
On some browsers, you instead need to click on the "File" menu and then on
"View Source". Try it with this page to see how I have applied the ideas I
explain below. You will find yourself developing a critical eye as many pages
look rather a mess under the hood!</p>

<p>For Mac users, before you can save a file with the ".html" extension,
you will need to ensure that your document is formatted as plain text.
For TextEdit, you can set this with the "Format" menu's  "Make Plain Text"
option.</p>

<p>This page will teach you how to:</p>
<ul>
  <li>start with a title</li>
  <li>add headings and paragraphs</li>
  <li>add emphasis to your text</li>
  <li>add images</li>
  <li>add links to other pages</li>

  <li>use various kinds of lists</li>
</ul>

<p>If you are looking for something else, try the <a
href="Advanced.html">advanced HTML</a> page.</p>

<h2>Start with a title</h2>

<p>Every HTML document needs a title. Here is what you need to type:</p>
<pre>&lt;title&gt;My first HTML document&lt;/title&gt;</pre>

<p>Change the text from "My first HTML document" to suit your own needs. The
title text is preceded by the start tag &lt;title&gt; and ends with the
matching end tag &lt;/title&gt;. The title should be placed at the beginning
of your document.</p>

<p>To try this out, type the above into a text editor and save the file as
"test.html", then view the file in a web browser. If the file extension is
".html" or ".htm" then the browser will recognize it as HTML. Most browsers
show the title in the window caption bar. With just a title, the browser will
show a blank page. Don't worry. The next section will show how to add
displayable content.</p>

<h2>Add headings and paragraphs</h2>

<p>If you have used Microsoft Word, you will be familiar with the built in
styles for headings of differing importance. In HTML there are six levels of
headings. H1 is the most important, H2 is slightly less important, and so on
down to H6, the least important.</p>

<p>Here is how to add an important heading:</p>
<pre>&lt;h1&gt;An important heading&lt;/h1&gt;</pre>

<p>and here is a slightly less important heading:</p>
<pre>&lt;h2&gt;A slightly less important heading&lt;/h2&gt;</pre>

<p>Each paragraph you write should start with a &lt;p&gt; tag. The &lt;/p&gt;

is optional, unlike the end tags for elements like headings. For example:</p>
<pre>&lt;p&gt;This is the first paragraph.&lt;/p&gt;

&lt;p&gt;This is the second paragraph.&lt;/p&gt;</pre>

<h2>Adding a bit of emphasis</h2>

<p>You can emphasize one or more words with the &lt;em&gt; tag, for
instance:</p>

<pre>This is a really &lt;em&gt;interesting&lt;/em&gt; topic!</pre>

<h2>Adding interest to your pages with images</h2>

<p>Images can be used to make your Web pages distinctive and greatly help to
get your message across. The simple way to add an image is using the
&lt;img&gt; tag. Let's assume you have an image file called "peter.jpg" in
the same folder/directory as your HTML file. It is 200 pixels wide by 150
pixels high.</p>
<pre>&lt;img src="peter.jpg" width="200" height="150"&gt;</pre>

<p>The src attribute names the image file. The width and height aren't
strictly necessary but help to speed the display of your Web page. Something
is still missing! People who can't see the image need a description they can
read in its absence. You can add a short description as follows:</p>
<pre>&lt;img src="peter.jpg" width="200" height="150"
alt="My friend Peter"&gt;</pre>

<p>The alt attribute is used to give the short description, in this case "My
friend Peter". For complex images, you may need to also give a longer
description. Assuming this has been written in the file "peter.html", you can
add one as follows using the longdesc attribute:</p>
<pre>&lt;img src="peter.jpg" width="200" height="150"
alt="My friend Peter" longdesc="peter.html"&gt;</pre>

<p>You can create images in a number of ways, for instance with a digital
camera, by scanning an image in, or creating one with a painting or drawing
program. Most browsers understand GIF and JPEG image formats, newer browsers
also understand the PNG image format. To avoid long delays while the image is
downloaded over the network, you should avoid using large image files.</p>

<p>Generally speaking, JPEG is best for photographs and other smoothly
varying images, while GIF and PNG are good for graphics art involving flat
areas of color, lines and text. All three formats support options for
progressive rendering where a crude version of the image is sent first and
progressively refined.</p>

<h2>Adding links to other pages</h2>

<p>What makes the Web so effective is the ability to define links from one
page to another, and to follow links at the click of a button. A single click
can take you right across the world!</p>

<p>Links are defined with the &lt;a&gt; tag. Lets define a link to the page
defined in the file "peter.html" in the same folder/directory as the HTML
file you are editing:</p>

<pre>This a link to &lt;a href="peter.html"&gt;Peter's page&lt;/a&gt;.</pre>

<p>The text between the &lt;a&gt; and the &lt;/a&gt; is used as the caption
for the link. It is common for the caption to be in blue underlined text.</p>

<p>If the file you are linking to is in a parent folder/directory, you need
to put "../" in front of it, for instance:</p>

<pre>&lt;a href="../mary.html"&gt;Mary's page&lt;/a&gt;</pre>

<p>If the file you are linking to is in a subdirectory, you need
to put the name of the subdirectory followed by a "/" in front of it,
for instance:</p>

<pre>&lt;a href="friends/sue.html"&gt;Sue's page&lt;/a&gt;</pre>

<p>The use of relative paths allows you to link to a file by walking
up and down the tree of directories as needed, for instance:</p>

<pre>&lt;a href="../college/friends/john.html"&gt;John's page&lt;/a&gt;</pre>

<p>Which first looks in the parent directory for another directory called
"college", and then at a subdirectory of that named "friends" for a file
called "john.html".</p>

<p>To link to a page on another Web site you need to give the full Web
address (commonly called a URL), for instance to link to www.w3.org you need
to write:</p>

<pre>This is a link to &lt;a href="http://www.w3.org/"&gt;W3C&lt;/a&gt;.</pre>

<p>You can turn an image into a hypertext link, for example, the following
allows you to click on the company logo to get to the home page:</p>

<pre>&lt;a href="/"&gt;&lt;img src="logo.gif" alt="home page"&gt;&lt;/a&gt;</pre>

<p>This uses "/" to refer to the root of the directory tree, i.e. the home page.</p>

<h2>Three kinds of lists</h2>

<p>HTML supports three kinds of lists. The first kind is a bulletted list,
often called an <em>unordered list</em>. It uses the &lt;ul&gt; and
&lt;li&gt; tags, for instance:</p>

<pre>&lt;ul&gt;
  &lt;li&gt;the first list item&lt;/li&gt;

  &lt;li&gt;the second list item&lt;/li&gt;

  &lt;li&gt;the third list item&lt;/li&gt;

&lt;/ul&gt;</pre>

<p>Note that you always need to end the list with the &lt;/ul&gt; end tag,
but that the &lt;/li&gt; is optional and can be left off. The second kind of
list is a numbered list, often called an <em> ordered list</em>. It uses the
&lt;ol&gt; and &lt;li&gt; tags. For instance:</p>

<pre>&lt;ol&gt;
  &lt;li&gt;the first list item&lt;/li&gt;

  &lt;li&gt;the second list item&lt;/li&gt;

  &lt;li&gt;the third list item&lt;/li&gt;

&lt;/ol&gt;</pre>

<p>Like bulletted lists, you always need to end the list with the &lt;/ol&gt;
end tag, but the &lt;/li&gt; end tag is optional and can be left off.</p>

<p>The third and final kind of list is the definition list. This allows you
to list terms and their definitions. This kind of list starts with a
&lt;dl&gt; tag and ends with &lt;/dl&gt; Each term starts with a &lt;dt&gt;

tag and each definition starts with a &lt;dd&gt;. For instance:</p>
<pre>&lt;dl&gt;
  &lt;dt&gt;the first term&lt;/dt&gt;
  &lt;dd&gt;its definition&lt;/dd&gt;

  &lt;dt&gt;the second term&lt;/dt&gt;
  &lt;dd&gt;its definition&lt;/dd&gt;

  &lt;dt&gt;the third term&lt;/dt&gt;
  &lt;dd&gt;its definition&lt;/dd&gt;

&lt;/dl&gt;</pre>

<p>The end tags &lt;/dt&gt; and &lt;/dd&gt; are optional and can be left off.
Note that lists can be nested, one within another. For instance:</p>
<pre>&lt;ol&gt;
  &lt;li&gt;the first list item&lt;/li&gt;

  &lt;li&gt;
    the second list item
    &lt;ul&gt;
      &lt;li&gt;first nested item&lt;/li&gt;
      &lt;li&gt;second nested item&lt;/li&gt;

    &lt;/ul&gt;
  &lt;/li&gt;

  &lt;li&gt;the third list item&lt;/li&gt;
&lt;/ol&gt;</pre>

<p>You can also make use of paragraphs and headings etc. for longer list
items.</p>

<h2>HTML has a head and a body</h2>

<p>If you use your web browser's view source feature (see the View or File
menus) you can see the structure of HTML pages. The document generally
starts with a declaration of which version of HTML has been used, and
is then followed by an &lt;html&gt; tag followed by &lt;head&gt; and
at the very end by &lt;/html&gt;. The &lt;html&gt; ... &lt;/html&gt; acts
like a container for the document. The &lt;head&gt; ... &lt;/head&gt;

contains the title, and information on style sheets and scripts, while
the &lt;body&gt; ... &lt;/body&gt; contains the markup with the visible
content. Here is a template you can copy and paste into your text
editor for creating your own pages:</p>

<pre class="template">
&lt;!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd"&gt;
&lt;html&gt;
&lt;head&gt;
  &lt;title&gt; <em>replace with your document's title</em> &lt;/title&gt;

&lt;/head&gt;
&lt;body&gt;

<em>replace with your document's content</em>

&lt;/body&gt;
&lt;/html&gt;
</pre>

<h2>Tidying up your markup</h2>

<p>A convenient way to automatically fix markup errors is to use <a
href="http://www.w3.org/People/Raggett/tidy/">HTML Tidy</a> which
also tidies the markup making it easier to read and easier to edit.
I recommend you regularly run Tidy over any markup you are editing.
Tidy is very effective at cleaning up markup created by authoring
tools with sloppy habits. Tidy is available for a wide range of
operating systems from the <a
href="http://tidy.sourceforge.net/#binaries">TidyLib Sourceforge
site</a>, and has also been integrated into a variety of HTML
editing tools.</p>

<h2>Getting Further Information</h2>

<p>If you are ready to learn more, I have prepared some accompanying material
on <a href="Advanced.html">advanced HTML</a> and <a href="Style.html">adding
a touch of style</a>.</p>

<p>W3C's Recommendation for <a href="http://www.w3.org/TR/REC-html40">HTML
4.0</a> is the authoritative specification for HTML. However, it is a technical
specification. For a less technical source of information you may want to
purchase one of the many books on HTML, for example "<a
href="http://www.w3.org/People/Raggett#htmlbook">Raggett on HTML 4</a>",
published 1998 by Addison Wesley.<!-- See also <em>"<a
href="http://www.wrox.com/Consumer/Store/Details.asp?ISBN=1861003439">Beginning
XHTML</a>"</em>, published 2000 by Wrox Press, which introduces W3C's
reformulation of HTML as an application of XML.--> <a href="/TR/xhtml1/">XHTML
1.0</a> is now a W3C Recommendation.</p>

<p>Best of luck and get writing!</p>

<p><i><a href="http://www.w3.org/People/Raggett">Dave Raggett</a> &lt;<a
href="mailto:dsr@w3.org">dsr@w3.org</a>&gt;</i></p>

<p class="policyfooter"><small><a rel="Copyright"
href="/Consortium/Legal/ipr-notice#Copyright">Copyright</a> &copy;
1994-2003 <a href="/"><acronym
title="World Wide Web Consortium">W3C</acronym></a><sup>&reg;</sup>
(<a href="http://www.lcs.mit.edu/"><acronym
title="Massachusetts Institute of Technology">MIT</acronym></a>, <a
href="http://www.ercim.org/"><acronym
title="European Research Consortium for Informatics and Mathematics">ERCIM</acronym></a>,
<a href="http://www.keio.ac.jp/">Keio</a>), All Rights Reserved. W3C <a
href="/Consortium/Legal/ipr-notice#Legal_Disclaimer">liability</a>, <a
href="/Consortium/Legal/ipr-notice#W3C_Trademarks">trademark</a>, <a
rel="Copyright" href="/Consortium/Legal/copyright-documents">document
use</a> and <a rel="Copyright"
href="/Consortium/Legal/copyright-software">software licensing</a>

rules apply. Your interactions with this site are in accordance with
our <a href="/Consortium/Legal/privacy-statement#Public">public</a> and
<a href="/Consortium/Legal/privacy-statement#Members">Member</a>
privacy statements.</small></p>
