<?php
 // file: index.php3
 // note: login screen... maybe move to login.php3??
 // code: jeff b (jeff@univrel.pr.uconn.edu)
 //       Max Klohn (amk@span.ch)
 // lic : GPL, v2

  $page_name = "index.php3";
  include "global.var.inc";
  include "freemed-functions.inc";

  SetCookie ("default_facility", "0", time()-100);

  freemed_display_html_top ();
  freemed_display_banner ();

  freemed_display_box_top(PACKAGENAME." "._("Login"), "index.php3");

echo "
<P>
<TABLE WIDTH=100% BORDER=0 CELLPADDING=2>
<TR><TD ALIGN=RIGHT>
<FORM ACTION=\"authenticate.php\" METHOD=POST>

<INPUT TYPE=HIDDEN NAME=\"__dummy\"
 VALUE=\"01234567890123456789012345678901234567890
        01234567890123456789012345678901234567890
        01234567890123456789012345678901234567890\">
<TT>"._("Username")." : </TT>
</TD><TD ALIGN=LEFT>
<INPUT TYPE=TEXT NAME=\"_u\" LENGTH=20 MAXLENGTH=32>
</TD></TR>
<TR><TD ALIGN=RIGHT>
<TT>"._("Password")." : </TT>
</TD><TD>
<INPUT TYPE=PASSWORD NAME=\"_p\" LENGTH=20 MAXLENGTH=32></TD></TR> 
<TR><TD ALIGN=RIGHT>
<TT>"._("Language")." : </TT>
</TD><TD ALIGN=LEFT>
<SELECT NAME=\"_l\">
 <OPTION VALUE=\"$language\">"._("Default Language")."
";

 // actually open the language registry
 $f_reg = fopen ("$physical_loc/lang/registry", "r");
 while ($f_line = fgets ($f_reg, 255)) {
   if (substr ($f_line, 0, 1) != "#") { // skip comments
     $f_line_array = explode (":", $f_line);
     echo " <OPTION VALUE=\"".prepare($f_line_array[0])."\">".
       prepare($f_line_array[1])."\n";
   } // end of skipping comments
 } // end while we have more lines to get
 fclose ($f_reg);

echo "
</SELECT>
</TR>
";

$Connection = fdb_connect (DB_HOST, DB_USER, DB_PASSWORD);
if (fdb_query ("SELECT * FROM config")) {
echo "
<TR><TD ALIGN=RIGHT>
<TT>"._("Facility")." : </TT>
</TD><TD ALIGN=LEFT>
<SELECT NAME=\"_f\">
".freemed_display_facilities ($_f, true, "0")."
</SELECT>
</TD></TR>
";

} // end checking for connection
fdb_close ();

 // 19990921 -- checking if persistant...
if (!empty($_URL))
 echo "
  <TR><TD ALIGN=RIGHT>
   <TT>"._("Resume")." : </TT>
  </TD><TD ALIGN=LEFT>
   <INPUT TYPE=RADIO NAME=\"_URL\" VALUE=\"$_URL\" CHECKED>"._("Resume")."<BR>
   <INPUT TYPE=RADIO NAME=\"_URL\" VALUE=\"\">"._("Reset Resume")."
  </TD></TR>
 ";

echo "
</TABLE>
<CENTER>
  <INPUT TYPE=SUBMIT VALUE=\""._("Enter the database")."\">
  <INPUT TYPE=RESET  VALUE=\""._("Clear")."\">
</CENTER>
</FORM>
";

  if ($debug) {
    echo "
      <TABLE WIDTH=100% BORDER=0 CELLSPACING=0 CELLPADDING=0
       VALIGN=BOTTOM ALIGN=CENTER BGCOLOR=\"#000000\">
      <TR><TD BGCOLOR=\"#000000\">
      <CENTER><B><FONT COLOR=\"#ffffff\" SIZE=-2>"._("DEBUG_IS_ON")."</FONT></B></CENTER>
      </TD></TR></TABLE>
    ";
  }
?>

</TD></TR></TABLE>
</TD></TR></TABLE>
</CENTER>

<BR>
<CENTER>
<A HREF="http://www.freemed.org"
 ><IMG SRC="img/tag-0.0.gif" BORDER=0 ALT="freemed!"></A>
<A HREF="http://www.php.net"
 ><IMG SRC="img/php4.gif" BORDER=0 ALT=""._("Powered by PHP").""></A>
<A HREF="http://www.vim.org"
 ><IMG SRC="img/vi.gif" BORDER=0 ALT="100% VI Meat Content"></A>
<?php
  if ($debug) {
    echo "
      <BR><FONT SIZE=-2>
      <A HREF=\"CHANGELOG\">CHANGELOG for ".VERSION."</A>
      </FONT>
    ";
  }
?>
</CENTER>

<?php freemed_display_html_bottom() ?>
