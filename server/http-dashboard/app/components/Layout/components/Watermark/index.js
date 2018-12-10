import React from "react";
import connect from "react-redux/es/connect/connect";

@connect((state) => ({
  Organization: state.Organization
}))
class Watermark extends React.Component {
  static propTypes = {
    Organization: React.PropTypes.object,
  };

  render() {
    const styles = {
      backgroundColor: '#' + this.props.Organization.primaryColor,
      color: '#' + this.props.Organization.secondaryColor,
      position: 'fixed',
      bottom: 0,
      right: 0,
      width: 'auto',
      // opacity: 0.7,
    };

    const watermarkDeploymentDate = `%(qa_watermark_deployment_date)`;
    const watermarkGitHash = `%(qa_watermark_deployment_date)`;

    return (
      <div style={styles}>
        {watermarkDeploymentDate}, {watermarkGitHash}
      </div>
    );
  }
}

export default Watermark;
