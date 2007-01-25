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

LoadObjectDependency('org.freemedsoftware.core.EMRModule');

class PatientCorrespondence extends EMRModule {

	var $MODULE_NAME    = "Patient Correspondence";
	var $MODULE_VERSION = "0.1";
	var $MODULE_FILE    = __FILE__;
	var $MODULE_UID     = "e5bf6501-e6ae-4d2e-ad87-9aab1911722a";

	var $PACKAGE_MINIMUM_VERSION = '0.7.0';

	var $record_name    = "Correspondence";
	var $table_name     = "patletter";
	var $patient_field  = "letterpatient";
	var $widget_hash    = "##letterdt## ##letterfrom:physician:phylname##";

	var $print_template = 'patient_correspondence';

	var $variables = array (
		"letterdt",
		"lettereoc",
		"letterfrom",
		"lettertext",
		"letterpatient",
		"locked" => '0'
	);

	public function __construct () {
		// __("Patient Correspondence")

		// Set vars for patient management summary
		$this->summary_vars = array (
			__("Date") => "letterdt",
			__("From")   => "letterfrom:physician"
		);
		$this->summary_options = SUMMARY_VIEW | SUMMARY_VIEW_NEWWINDOW
			| SUMMARY_PRINT | SUMMARY_LOCK | SUMMARY_DELETE;

		// Set associations
		$this->_SetAssociation('EpisodeOfCare');
		$this->_SetMetaInformation('EpisodeOfCareVar', 'lettereoc');

		// Set ACL for billers + EMR access
		$this->acl = array ( 'bill', 'emr' );

		// Run parent constructor
		parent::__construct ( );
	} // end constructor PatientCorrespondence

	protected function add_pre ( &$data ) {
		if ($data['worddoc']) {
			$docfile = tempnam ( '/tmp', 'wordconv' );
			file_put_contents ( $docfile, $data['worddoc'] );

			// Convert to the temporary file
			$__command = "/usr/bin/wvWare -x /usr/share/wv/wvText.xml \"$docfile\"";
			$data['lettertext'] = `$__command`;

			// Remove uploaded document
			@unlink( $docfile );
		} // end checking for uploaded msworddoc
	}

	// ----- Internal update

	function _update() {
		global $sql;
		$version = freemed::module_version($this->MODULE_NAME);
		// Version xxxx
		//
		/*
		if (!version_check($version, '0.2')) {
			$sql->query('ALTER TABLE '.$this->table_name.' '.
				'ADD COLUMN locked INT UNSIGNED AFTER letterpatient');
		}
		*/
	} // end method _update

} // end class PatientCorrespondence

register_module ("PatientCorrespondence");

?>
