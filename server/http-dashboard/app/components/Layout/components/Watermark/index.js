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
      // color: '#' + this.props.Organization.secondaryColor,
      position: 'fixed',
      bottom: 0,
      right: 0,
      width: 'auto',
      padding: 5,
      // opacity: 0.7,
    };

    return (
      <div style={styles}>
        <div>
          Deployment Date: {process.env.BLYNK_DEPLOYMENT_DATE}
        </div>
        <div>
          Git Commit Hash: {process.env.BLYNK_COMMIT_HASH}
        </div>
        <div>
          Git Commit Date: {process.env.BLYNK_COMMIT_DATE}
        </div>
      </div>
    );
  }
}

export default Watermark;
