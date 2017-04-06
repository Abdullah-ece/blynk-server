import React from 'react';

import './styles.scss';
import LogoUploader from "components/LogoUploader/index";
import BrandingColorPicker from "components/BrandingColorPicker/index";

export default class OrganizationBranding extends React.Component {
  render() {
    return (
      <div className="user-profile--organization-settings--organization-branding">
        <LogoUploader/>
        <div className="user-profile--organization-settings--organization-branding-colors">
          <BrandingColorPicker title="primary color"/>
          <BrandingColorPicker title="secondary color"/>
        </div>
      </div>
    );
  }
}
