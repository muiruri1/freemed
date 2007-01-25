<?php
 // $Id$
 //
 // Authors:
 // 	Jeff Buchbinder <jeff@freemedsoftware.org>
 //
 // FreeMED Electronic Medical Record and Practice Management System
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

LoadObjectDependency('org.freemedsoftware.core.SupportModule');

class ProviderGroups extends SupportModule {

	var $MODULE_NAME    = "Provider Groups";
	var $MODULE_VERSION = "0.1";
	var $MODULE_FILE    = __FILE__;
	var $MODULE_UID     = "710abf21-d584-41a9-a579-5dc2d8d80310";

	var $PACKAGE_MINIMUM_VERSION = '0.8.0';

	var $table_name     = "phygroup";
	var $record_name    = "Provider Group";
	var $order_field    = "phygroupname";

	var $variables = array (
		"phygroupname",
		"phygroupfac",
		"phygroupdtadd",
		"phygroupdtmod",
		"phygroupidmap",
		"phygroupdocs",
		"phygroupspe1"
	);

	public function __construct () {
		// For i18n: __("Provider Groups")

		global $phygroupdtmod;

		$this->list_view = array (
			__("Physician Group Name") => "phygroupname",
			__("Default Facility")     => "phygroupfac"
		);

		// Run constructor
		parent::__construct();
	} // end constructor

	protected function add_pre ( &$data ) {
		$data['phygroupidmap'] = serialize ( $data['phygroupidmap'] );
		$data['phygroupidmap'] = join ( ',', $data['phygroupdocs'] );
		$data['phygroupdtadd'] = date("Y-m-d");
	}

	protected function mod_pre ( &$data ) {
		$data['phygroupidmap'] = serialize ( $data['phygroupidmap'] );
		$data['phygroupidmap'] = join ( ',', $data['phygroupdocs'] );
		$data['phygroupdtmod'] = date("Y-m-d");
	}

} // end class ProviderGroups

register_module ("ProviderGroups");

?>
