<!--{* Smarty *}-->
<!--{*
 // $Id$
 //
 // Authors:
 //      Jeff Buchbinder <jeff@freemedsoftware.org>
 //
 // Copyright (C) 1999-2007 FreeMED Software Foundation
 //
 // This program is free software; you can redistribute it and/or modify
 // it under the terms of the GNU General Public License as published by
 // the Free Software Foundation; either version 2 of the License, or
 // (at your option) any later version.
 //
 // This program is distributed in the hope that it will be useful,
 // but WITHOUT ANY WARRANTY; without even the implied warranty of
 // MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 // GNU General Public License for more details.
 //
 // You should have received a copy of the GNU General Public License
 // along with this program; if not, write to the Free Software
 // Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*}-->

<script type="text/javascript">
	var m = {
		handleResponse: function ( data ) {
			if (data) {
				<!--{if $id}-->
				freemedMessage( "<!--{t}-->Committed changes.<!--{/t}-->", "INFO" );
				<!--{else}-->
				freemedMessage( "<!--{t}-->Added record.<!--{/t}-->", "INFO" );
				<!--{/if}-->
				freemedLoad( 'org.freemedsoftware.ui.supportdata.list?module=<!--{$module}-->' );
			} else {
				dojo.widget.byId('ModuleFormCommitChangesButton').enable();
			}
		},
		validate: function ( content ) {
			var r = true;
			var m = "";
			if ( content.providername.length < 2 ) {
				m += "<!--{t}-->You must enter a name.<!--{/t}-->\n";
				r = false;
			}
			if ( content.mailgwaddr.length < 2 ) {
				m += "<!--{t}-->You must enter a gateway.<!--{/t}-->\n";
				r = false;
			}
			if ( m.length > 1 ) { alert( m ); }
			return r;
		},
		initialLoad: function ( ) {
			<!--{if $id}-->
			dojo.io.bind({
				method: "POST",
				content: {
					param0: "<!--{$id|escape}-->"
				},
				url: "<!--{$relay}-->/org.freemedsoftware.module.smsprovider.GetRecord",
				load: function ( type, data, evt ) {
					document.getElementById( 'providername' ).value = data.providername;
					document.getElementById( 'numberlength' ).value = parseInt( data.numberlength );
					document.getElementById( 'mailgwaddr' ).value = data.mailgwaddr;
					document.getElementById( 'countrycode' ).selectedIndex = data.countrycode;
				},
				mimetype: "text/json",
				sync: true
			});
			<!--{/if}-->
		},
		submit: function ( ) {
			try {
				dojo.widget.byId('ModuleFormCommitChangesButton').disable();
			} catch ( err ) { }
			var myContent = {
				<!--{if $id}-->id: "<!--{$id|escape}-->",<!--{/if}-->
				providername: document.getElementById('providername').value,
				mailgwaddr: document.getElementById('mailgwaddr').value,
				numberlength: parseInt( document.getElementById('numberlength').value ),
				countrycode: document.getElementById('countrycode').value
			};
			if (m.validate( myContent )) {
				dojo.io.bind({
					method: "POST",
					content: {
						param0: myContent
					},
					url: "<!--{$relay}-->/org.freemedsoftware.module.smsprovider.<!--{if $id}-->mod<!--{else}-->add<!--{/if}-->",
					load: function ( type, data, evt ) {
						m.handleResponse( data );
					},
					mimetype: "text/json"
				});
			}
		}
	};

	_container_.addOnLoad(function() {
		m.initialLoad();
		dojo.event.connect( dojo.widget.byId('ModuleFormCommitChangesButton'), 'onClick', m, 'submit' );
	});
	_container_.addOnUnload(function() {
		dojo.event.disconnect( dojo.widget.byId('ModuleFormCommitChangesButton'), 'onClick', m, 'submit' );
	});

</script>

<h3><!--{t}-->SMS Provider<!--{/t}--></h3>

<table border="0" style="width: auto;">

	<tr>
		<td align="right"><!--{t}-->Provider Name<!--{/t}--></td>
		<td><input type="text" id="providername" size="50" maxlength="250" /></td>
	</tr>

	<tr>
		<td align="right"><!--{t}-->Gateway<!--{/t}--></td>
		<td><input type="text" id="mailgwaddr" size="50" /></td>
	</tr>

	<tr>
		<td align="right"><!--{t}-->Number Length<!--{/t}--></td>
		<td><input type="text" id="numberlength" /></td>
	</tr>

	<tr>
		<td align="right"><!--{t}-->Use Country Code?<!--{/t}--></td>
		<td><select id="countrycode">
			<option value="0"><!--{t}-->No<!--{/t}--></option>
			<option value="1"><!--{t}-->Yes<!--{/t}--></option>
		</select></td>
	</tr>

</table>

<div align="center">
        <table border="0" style="width:200px;">
        <tr><td align="center">
	        <button dojoType="Button" id="ModuleFormCommitChangesButton" widgetId="ModuleFormCommitChangesButton">
	                <div><!--{t}-->Commit Changes<!--{/t}--></div>
	        </button>
        </td><td align="left">
        	<button dojoType="Button" id="ModuleFormCancelButton" widgetId="ModuleFormCancelButton" onClick="freemedLoad( 'org.freemedsoftware.ui.supportdata.list?module=<!--{$module}-->' );">
        	        <div><!--{t}-->Cancel<!--{/t}--></div>
        	</button>
        </td></tr></table>
</div>

