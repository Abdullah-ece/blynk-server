import React from 'react';

import './styles.less';
import LogoUploader from "components/LogoUploader/index";
import BrandingColorPicker from "components/BrandingColorPicker/index";
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import {OrganizationBrandingUpdate, OrganizationSave, OrganizationLogoUpdate} from 'data/Organization/actions';

@connect((state) => ({
  primaryColor: state.Organization.primaryColor,
  secondaryColor: state.Organization.secondaryColor,
  logo: state.Organization.logoUrl,
  Organization: state.Organization,
}), (dispatch) => ({
  OrganizationBrandingUpdate: bindActionCreators(OrganizationBrandingUpdate, dispatch),
  OrganizationLogoUpdate: bindActionCreators(OrganizationLogoUpdate, dispatch),
  OrganizationSave: bindActionCreators(OrganizationSave, dispatch)
}))
export default class OrganizationBranding extends React.Component {
  static propTypes = {
    Organization: React.PropTypes.object,
    primaryColor: React.PropTypes.any,
    secondaryColor: React.PropTypes.any,
    OrganizationBrandingUpdate: React.PropTypes.func,
    OrganizationSave: React.PropTypes.func,
    OrganizationLogoUpdate: React.PropTypes.func,
    logo: React.PropTypes.string
  };

  DEFAULT_LOGO = '/static/logo.png';

  handlePrimaryColorChange(primaryColor) {
    const data = {
      primaryColor: primaryColor,
      secondaryColor: this.props.secondaryColor
    };
    this.props.OrganizationBrandingUpdate(data);
    this.props.OrganizationSave(Object.assign({}, this.props.Organization, data));
  }

  handleSecondaryColorChange(secondaryColor) {
    const data = {
      primaryColor: this.props.primaryColor,
      secondaryColor: secondaryColor
    };
    this.props.OrganizationBrandingUpdate(data);
    this.props.OrganizationSave(Object.assign({}, this.props.Organization, data));
  }

  handleLogoChange(logoUrl) {
    this.props.OrganizationLogoUpdate(logoUrl || this.DEFAULT_LOGO);
    this.props.OrganizationSave(Object.assign({}, this.props.Organization, {
      logoUrl: logoUrl || this.DEFAULT_LOGO
    }));
  }

  render() {
    return (
      <div className="user-profile--organization-settings--organization-branding">
        <LogoUploader onChange={this.handleLogoChange.bind(this)} logo={this.props.logo}
                      defaultImage={this.DEFAULT_LOGO}/>
        <div className="user-profile--organization-settings--organization-branding-colors">
          <BrandingColorPicker title="primary color" color={this.props.primaryColor}
                               onChange={this.handlePrimaryColorChange.bind(this)}/>
          <BrandingColorPicker title="secondary color" color={this.props.secondaryColor}
                               onChange={this.handleSecondaryColorChange.bind(this)}/>
        </div>
      </div>
    );
  }
}
