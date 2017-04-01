import CryptoJS from 'crypto-js';

export const encryptUserPassword = (email, password) => {

  const algo = CryptoJS.algo.SHA256.create();

  algo.update(password, 'utf-8');
  algo.update(CryptoJS.SHA256(email.toLowerCase()), 'utf-8');

  return algo.finalize().toString(CryptoJS.enc.Base64);

};
