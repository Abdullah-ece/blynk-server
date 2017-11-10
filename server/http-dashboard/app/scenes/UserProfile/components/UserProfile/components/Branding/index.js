import React from 'react';

import './styles.less';
import LogoUploader from "components/LogoUploader/index";
import BrandingColorPicker from "components/BrandingColorPicker/index";

export default class OrganizationBranding extends React.Component {
  static propTypes = {
    Organization: React.PropTypes.object,
    onOrganizationSave:React.PropTypes.func,
    onOrganizationLogoUpdate: React.PropTypes.func,
    onOrganizationBrandingUpdate:React.PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleLogoChange = this.handleLogoChange.bind(this);
    this.handlePrimaryColorChange = this.handlePrimaryColorChange.bind(this);
    this.handleSecondaryColorChange = this.handleSecondaryColorChange.bind(this);
  }

  DEFAULT_LOGO = '/static/logo.png';

  handlePrimaryColorChange(primaryColor) {
    const data = {
      primaryColor: primaryColor,
      secondaryColor: this.props.Organization.secondaryColor
    };
    this.props.onOrganizationBrandingUpdate(data);
    this.props.onOrganizationSave(Object.assign({}, this.props.Organization, data));
  }

  handleSecondaryColorChange(secondaryColor) {
    const data = {
      primaryColor: this.props.Organization.primaryColor,
      secondaryColor: secondaryColor
    };
    this.props.onOrganizationBrandingUpdate(data);
    this.props.onOrganizationSave(Object.assign({}, this.props.Organization, data));
  }

  handleLogoChange(logoUrl) {
    this.props.onOrganizationLogoUpdate(logoUrl || this.DEFAULT_LOGO);
    this.props.onOrganizationSave(Object.assign({}, this.props.Organization, {
      logoUrl: logoUrl || this.DEFAULT_LOGO
    }));
  }

  render() {
    return (
      <div className="user-profile--organization-settings--organization-branding">
        <LogoUploader onChange={this.handleLogoChange}
                      logo={this.props.Organization.logoUrl}
                      defaultImage={this.DEFAULT_LOGO}/>
        <div className="user-profile--organization-settings--organization-branding-colors">
          <BrandingColorPicker title="primary color"
                               color={this.props.Organization.primaryColor}
                               onChange={this.handlePrimaryColorChange}/>

          <BrandingColorPicker title="secondary color"
                               color={this.props.Organization.secondaryColor}
                               onChange={this.handleSecondaryColorChange}/>
        </div>
      </div>
    );
  }
}
