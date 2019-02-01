import React from 'react';

class EditSection extends React.Component {
  render() {
    return (
      <div className="edit-section">
        {this.props.title && (<div className="edit-section-title">
          {this.props.title}
        </div>)}
        <div className="edit-section-content">
          {this.props.children}
        </div>
      </div>
    );
  }
}

export default EditSection;
